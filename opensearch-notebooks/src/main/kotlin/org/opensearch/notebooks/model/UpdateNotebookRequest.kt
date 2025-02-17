/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.notebooks.model

import org.opensearch.notebooks.NotebooksPlugin.Companion.LOG_PREFIX
import org.opensearch.notebooks.model.RestTag.NOTEBOOK_FIELD
import org.opensearch.notebooks.model.RestTag.NOTEBOOK_ID_FIELD
import org.opensearch.notebooks.util.createJsonParser
import org.opensearch.notebooks.util.logger
import org.opensearch.action.ActionRequest
import org.opensearch.action.ActionRequestValidationException
import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.ToXContentObject
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentFactory
import org.opensearch.common.xcontent.XContentParser
import org.opensearch.common.xcontent.XContentParser.Token
import org.opensearch.common.xcontent.XContentParserUtils
import java.io.IOException

/**
 * Notebook-update request.
 * notebookId is from request query params
 * <pre> JSON format
 * {@code
 * {
 *   "notebookId":"notebookId",
 *   "notebook":{
 *      // refer [org.opensearch.notebooks.model.Notebook]
 *   }
 * }
 * }</pre>
 */
internal class UpdateNotebookRequest : ActionRequest, ToXContentObject {
    val notebookId: String
    val notebook: Notebook

    companion object {
        private val log by logger(CreateNotebookRequest::class.java)
    }

    constructor(notebookId: String, notebook: Notebook) : super() {
        this.notebookId = notebookId
        this.notebook = notebook
    }

    @Throws(IOException::class)
    constructor(input: StreamInput) : this(input.createJsonParser())

    /**
     * Parse the data from parser and create [UpdateNotebookRequest] object
     * @param parser data referenced at parser
     */
    constructor(parser: XContentParser, useNotebookId: String? = null) : super() {
        var notebookId: String? = useNotebookId
        var notebook: Notebook? = null
        XContentParserUtils.ensureExpectedToken(Token.START_OBJECT, parser.currentToken(), parser)
        while (Token.END_OBJECT != parser.nextToken()) {
            val fieldName = parser.currentName()
            parser.nextToken()
            when (fieldName) {
                NOTEBOOK_ID_FIELD -> notebookId = parser.text()
                NOTEBOOK_FIELD -> notebook = Notebook.parse(parser)
                else -> {
                    parser.skipChildren()
                    log.info("$LOG_PREFIX:Skipping Unknown field $fieldName")
                }
            }
        }
        notebookId ?: throw IllegalArgumentException("$NOTEBOOK_ID_FIELD field absent")
        notebook ?: throw IllegalArgumentException("$NOTEBOOK_FIELD field absent")
        this.notebookId = notebookId
        this.notebook = notebook
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun writeTo(output: StreamOutput) {
        toXContent(XContentFactory.jsonBuilder(output), ToXContent.EMPTY_PARAMS)
    }

    /**
     * create XContentBuilder from this object using [XContentFactory.jsonBuilder()]
     * @param params XContent parameters
     * @return created XContentBuilder object
     */
    fun toXContent(params: ToXContent.Params = ToXContent.EMPTY_PARAMS): XContentBuilder? {
        return toXContent(XContentFactory.jsonBuilder(), params)
    }

    /**
     * {@inheritDoc}
     */
    override fun toXContent(builder: XContentBuilder?, params: ToXContent.Params?): XContentBuilder {
        return builder!!.startObject()
            .field(NOTEBOOK_ID_FIELD, notebookId)
            .field(NOTEBOOK_FIELD, notebook)
            .endObject()
    }

    /**
     * {@inheritDoc}
     */
    override fun validate(): ActionRequestValidationException? {
        return null
    }
}
