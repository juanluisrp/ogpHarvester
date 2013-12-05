package org.opengeoportal.harvester.api.component.webdav;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import org.jdom.JDOMException;
import org.opengeoportal.harvester.api.component.BaseIngestJob;
import org.opengeoportal.harvester.api.metadata.parser.Iso19139MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParser;
import org.opengeoportal.harvester.api.metadata.parser.MetadataParserResponse;
import org.opengeoportal.harvester.api.domain.IngestWebDav;
import org.opengeoportal.harvester.api.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;


public class WebdavIngestJob extends BaseIngestJob {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        Sardine sardine = null;
        try {
            sardine = SardineFactory.begin();
            processWebdavFolder(sardine, ingest.getUrl());
        } finally {
            if (sardine != null) sardine.shutdown();
        }
    }

    /**
     * Process the metadata files in the webdav server, recursing the subfolders.
     *
     * @param sardine
     * @param url
     * @throws IOException
     * @throws JDOMException
     */
    private void processWebdavFolder(Sardine sardine, String url) {
        if (!url.endsWith("/")) url += "/";
        List<DavResource> resources = null;
        try {
            resources = sardine.list(url);

        } catch (IOException ex) {
            // TODO: Add error in the ingest report
            return;
        }

        for (DavResource res : resources) {
            if (res.isDirectory()) {
                // If it's not the current folder, process the files inside
                if (!url.endsWith(res.getPath())) {
                    logger.debug("Processing webdav folder resource: " + res.toString());
                    try {
                        String absoluteHrefPath = getResourceAbsoluteUrl(res, url);
                        processWebdavFolder(sardine, absoluteHrefPath);

                    } catch (MalformedURLException ex) {
                        // TODO: Add error in the ingest report
                    }
                }
            } else {
                if (hasToProcessFile(res)) {
                    logger.debug("Processing webdav resource: " + res.toString());
                    processFile(res, url);
                } else {
                    logger.debug("Ignoring webdav resource: " + res.toString());
                }
            }
        }
    }


    /**
     * Process the remote WebDav file, validating it and ingesting in the system.
     *
     * @param res       WebDav resource.
     * @param baseUrl   Base url of the WebDav resource.
     */
    private void processFile(DavResource res, String baseUrl) {
        try {
            // Retrieve file content
            String absoluteHrefPath = getResourceAbsoluteUrl(res, baseUrl);

            Document document = XmlUtil.load(absoluteHrefPath);
            MetadataParser parser = new Iso19139MetadataParser();
            MetadataParserResponse parserResult = parser.parse(document);

            boolean valid = metadataValidator.validate(parserResult.getMetadata(), report);
            if (valid) metadataIngester.ingest(parserResult.getMetadata());

        } catch (Exception ex1) {
            // TODO: Add error in the ingest report
        }
    }


    /**
     * Checks if the file has to be processed, verifying the content type (application/xml)
     * and the date filter configured in the the Ingest.
     *
     * @param res   WebDav resource.
     * @return
     */
    private boolean hasToProcessFile(DavResource res) {
        if (res.isDirectory()) return false;
        if (!res.getContentType().equalsIgnoreCase("application/xml")) return false;

        IngestWebDav ingestWebdav = (IngestWebDav) ingest;
        Date beginFilterDate = ingestWebdav.getDateFrom();
        Date endFilterDate = ingestWebdav.getDateTo();
        Date resourceDate = res.getModified();

        if ((beginFilterDate != null) && (resourceDate.before(beginFilterDate))) return false;
        if ((endFilterDate != null) && (resourceDate.after(endFilterDate))) return false;

        return true;
    }

    /**
     * DavResource contains only the relative path. This method uses the base url
     * to return the absolute url of the resource.
     *
     * @param res        WebDav resource.
     * @param baseUrl    Base url of the WebDav resource.
     * @return           Absolute url of the WebDav resource.
     * @throws MalformedURLException
     */
    private String getResourceAbsoluteUrl(DavResource res, String baseUrl) throws MalformedURLException {
        URL urlR = new URL(baseUrl);

        return urlR + res.getHref().toString().replace(urlR.getPath(), "");
    }
}