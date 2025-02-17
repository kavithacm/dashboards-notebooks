/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.rest

import org.opensearch.notebooks.PluginRestTestCase
import org.opensearch.notebooks.NotebooksPlugin.Companion.BASE_NOTEBOOKS_URI
import org.opensearch.notebooks.constructNotebookRequest
import org.opensearch.notebooks.jsonify
import org.opensearch.notebooks.validateErrorResponse
import org.opensearch.notebooks.validateTimeRecency
import org.opensearch.rest.RestRequest
import org.opensearch.rest.RestStatus
import org.junit.Assert
import java.time.Instant

class NotebooksIT : PluginRestTestCase() {
    private fun createNotebook(name: String = "test"): String {
        val notebookCreateRequest = constructNotebookRequest(name)
        val notebookCreateResponse = executeRequest(
            RestRequest.Method.POST.name,
            "$BASE_NOTEBOOKS_URI/notebook",
            notebookCreateRequest,
            RestStatus.OK.status
        )
        val notebookId = notebookCreateResponse.get("notebookId").asString
        Assert.assertNotNull("notebookId should be generated", notebookId)
        Thread.sleep(100)
        return notebookId
    }

    fun `test create notebook`() {
        createNotebook()

        val notebookInvalidCreateResponse = executeRequest(
            RestRequest.Method.POST.name,
            "$BASE_NOTEBOOKS_URI/notebook",
            "",
            RestStatus.BAD_REQUEST.status
        )
        validateErrorResponse(notebookInvalidCreateResponse, RestStatus.BAD_REQUEST.status, "parse_exception")
        Thread.sleep(100)
    }

    fun `test get notebook`() {
        val notebookCreateRequest = constructNotebookRequest()
        val notebookCreateResponse = executeRequest(
            RestRequest.Method.POST.name,
            "$BASE_NOTEBOOKS_URI/notebook",
            notebookCreateRequest,
            RestStatus.OK.status
        )
        val notebookId = notebookCreateResponse.get("notebookId").asString
        Assert.assertNotNull("notebookId should be generated", notebookId)
        Thread.sleep(100)

        val notebooksGetResponse = executeRequest(
            RestRequest.Method.GET.name,
            "$BASE_NOTEBOOKS_URI/notebook/$notebookId",
            "",
            RestStatus.OK.status
        )
        val notebookDetails = notebooksGetResponse.get("notebookDetails").asJsonObject
        Assert.assertEquals(notebookId, notebookDetails.get("id").asString)
        Assert.assertEquals(
            jsonify(notebookCreateRequest).get("notebook").asJsonObject,
            notebookDetails.get("notebook").asJsonObject
        )
        validateTimeRecency(Instant.ofEpochMilli(notebookDetails.get("lastUpdatedTimeMs").asLong))
        validateTimeRecency(Instant.ofEpochMilli(notebookDetails.get("createdTimeMs").asLong))
        Thread.sleep(100)

        val notebooksInvalidGetResponse = executeRequest(
            RestRequest.Method.GET.name,
            "$BASE_NOTEBOOKS_URI/notebook/invalid-id",
            "",
            RestStatus.NOT_FOUND.status
        )
        validateErrorResponse(notebooksInvalidGetResponse, RestStatus.NOT_FOUND.status)
        Thread.sleep(100)
    }

    fun `test update notebook`() {
        val notebookId = createNotebook()

        val newName = "updated_name"
        val notebookUpdateRequest = constructNotebookRequest(newName)
        val notebookUpdateResponse = executeRequest(
            RestRequest.Method.PUT.name,
            "$BASE_NOTEBOOKS_URI/notebook/$notebookId",
            notebookUpdateRequest,
            RestStatus.OK.status
        )
        Assert.assertNotNull(notebookId, notebookUpdateResponse.get("notebookId").asString)
        Thread.sleep(100)

        val notebooksGetResponse = executeRequest(
            RestRequest.Method.GET.name,
            "$BASE_NOTEBOOKS_URI/notebook/$notebookId",
            "",
            RestStatus.OK.status
        )
        val notebookDetails = notebooksGetResponse.get("notebookDetails").asJsonObject
        Assert.assertEquals(notebookId, notebookDetails.get("id").asString)
        Assert.assertEquals(
            newName,
            notebookDetails.get("notebook").asJsonObject.get("name").asString
        )
        Thread.sleep(100)

        val notebooksInvalidUpdateResponse = executeRequest(
            RestRequest.Method.PUT.name,
            "$BASE_NOTEBOOKS_URI/notebook/invalid-id",
            notebookUpdateRequest,
            RestStatus.NOT_FOUND.status
        )
        validateErrorResponse(notebooksInvalidUpdateResponse, RestStatus.NOT_FOUND.status)
        Thread.sleep(100)
    }

    fun `test delete notebook`() {
        val notebookId = createNotebook()

        val notebookDeleteResponse = executeRequest(
            RestRequest.Method.DELETE.name,
            "$BASE_NOTEBOOKS_URI/notebook/$notebookId",
            "",
            RestStatus.OK.status
        )
        Assert.assertEquals(notebookId, notebookDeleteResponse.get("notebookId").asString)
        Thread.sleep(100)

        val notebookInvalidDeleteResponse = executeRequest(
            RestRequest.Method.DELETE.name,
            "$BASE_NOTEBOOKS_URI/notebook/$notebookId",
            "",
            RestStatus.NOT_FOUND.status
        )
        validateErrorResponse(notebookInvalidDeleteResponse, RestStatus.NOT_FOUND.status)
        Thread.sleep(100)
    }

    fun `test get all notebooks`() {
        val notebooksGetAllEmptyResponse = executeRequest(
            RestRequest.Method.GET.name,
            "$BASE_NOTEBOOKS_URI/notebooks",
            "",
            RestStatus.OK.status
        )
        Assert.assertEquals(0, notebooksGetAllEmptyResponse.get("totalHits").asInt)

        val notebookIds = Array(5) { createNotebook("test-$it") }
        Thread.sleep(1000)

        val notebooksGetAllResponse = executeRequest(
            RestRequest.Method.GET.name,
            "$BASE_NOTEBOOKS_URI/notebooks",
            "",
            RestStatus.OK.status
        )
        Assert.assertEquals(5, notebooksGetAllResponse.get("totalHits").asInt)
        val notebooksList = notebooksGetAllResponse.get("notebookDetailsList").asJsonArray
        Assert.assertArrayEquals(notebookIds, notebooksList.map { it.asJsonObject.get("id").asString }.toTypedArray())
    }
}
