/*
 * IngestFormBean.java
 *
 * Copyright (C) 2013
 *
 * This file is part of Open Geoportal Harvester.
 *
 * This software is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * As a special exception, if you link this library with other files to produce
 * an executable, this library does not by itself cause the resulting executable
 * to be covered by the GNU General Public License. This exception does not
 * however invalidate any other reasons why the executable file might be covered
 * by the GNU General Public License.
 *
 * Authors:: Jose García (mailto:jose.garcia@geocat.net)
 */
package org.opengeoportal.harvester.api.service;

import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import org.opengeoportal.harvester.api.domain.CustomRepository;
import org.opengeoportal.harvester.api.domain.Ingest;
import org.opengeoportal.harvester.api.domain.InstanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.google.common.collect.ListMultimap;

public interface CustomRepositoryService {
	public CustomRepository save(CustomRepository customRepository);

	/**
	 * Mark {@link CustomRepository} with id <code>id</code> passed and
	 * unschedule all {@link Ingest} that use that custom repo.
	 * 
	 * @param id custom repository identifier.
	 */
	public void logicalDelete(Long id);

	public CustomRepository findByName(String name);

	public Page<CustomRepository> findAll(Pageable pageable);

	public ListMultimap<InstanceType, CustomRepository> getAllGroupByType();

	/**
	 * Fetch the URL and search remote instances in the catalog.
	 * 
	 * @param repoType
	 * @param urlObj
	 * @return
	 */
	public List<SimpleEntry<String, String>> getRemoteRepositories(
			InstanceType repoType, URL urlObj);


	/**
	 * Fetch the remote repository with <code>repoID</code> and connect to its
	 * URL looking for remote sources.
	 * 
	 * @param repoId
	 * @return
	 */
	public List<SimpleEntry<String, String>> getRemoteRepositoriesByRepoId(
			Long repoId);

	/**
	 * Retrieve the {@link CustomRepository} with the identifier passed.
	 * 
	 * @param id
	 *            the {@link CustomRepository} identifier.
	 * @return the {@link CustomRepository} or null if can not be found.
	 */
	public CustomRepository findById(Long id);

	/**
	 * Check if exists a non deleted {@link CustomRepository} with the same name
	 * and service type.
	 * 
	 * @param name
	 *            the name.
	 * @param type
	 *            the service type.
	 * @return <code>true</code> if does not exist any CustomRepository with
	 *         deleted=false, and the name and type passed, <code>false</code>
	 *         otherwise.
	 */
	public boolean checkExistActiveRepositoryNameAndType(String name,
			InstanceType type);
}
