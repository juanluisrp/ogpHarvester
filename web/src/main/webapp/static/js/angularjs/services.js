'use strict';

/* Services */


// Demonstrate how to register services
// In this case it is a simple value service.
var servicesModule = angular.module('ogpHarvester.services', ['ngResource']);

servicesModule.factory('Ingest',
	function ($resource) {
		return $resource('rest/ingests/:id', {}, {
			'query': {
				method: 'GET',
				url: 'rest/ingests',
				isArray: true
			}

		});
	})
	.value('version', '0.1');



servicesModule.service('ingestMultiform',
	function () {
		// Private data

		var initBean = function () {
			console.log("Initiating ingest bean");
			var bean = {
				typeOfInstance: 'solr',
				catalogOfServices: null,
				solrUrl: null,
				extent: null,
				themeKeyword: null,
				placeKeyword: null,
				topic: null,
				rangeFrom: null,
				rangeTo: null,
				originator: null,
				dataTypes: [],
				dataRepositories: [],
				excludeRestricted: false,
				rangeSolrFrom: null,
				rangeSolrTo: null,
				requiredFields: {},
				geonetworkUrl: null,
				dataRepositoriesGN: [],
				gnTitle: null,
				gnKeyword: null,
				gnAbstractText: null,
				gnFreeText: null,
				gnSources: null,
				cswUrl: null,
				cswDataRepositories: [],
				cswLocation: null,
				cswTitle: null,
				cswSubject: null,
				cswFreeText: null,
				cswRangeFrom: null,
				cswRangeTo: null,
				cswCustomQuery: null,
				webdavUrl: null,
				webdavDataRepositories: [],
				webdavFromLastModified: null,
				webdavToLastModified: null,
				ingestName: null,
				beginDate: null,
				frequency: 'once'
			};
			return bean;
		};

		var ingest = initBean();


		// Public interface
		return {
			reset: function () {
				ingest = initBean();
			},

			getIngest: function () {
				return ingest;
			},
			validate: function () {

			}
		};


	});