/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import { IRouter, IOpenSearchDashboardsResponse, ResponseError } from '../../../../src/core/server';
import { API_PREFIX, FETCH_SIZE } from '../../common';
import { RequestParams } from '@elastic/elasticsearch';

export function vizRouter(router: IRouter) {
  // Fetches available saved visualizations for current user
  router.get(
    {
      path: `${API_PREFIX}/visualizations`,
      validate: {},
    },
    async (context, request, response): Promise<IOpenSearchDashboardsResponse<any | ResponseError>> => {
      const params: RequestParams.Search = {
        index: '.kibana',
        size: FETCH_SIZE,
        q: 'type:visualization',
      };
      try {
        const opensearchClientResponse = await context.core.opensearch.legacy.client.callAsCurrentUser(
          'search',
          params
        );
        const savedVisualizations = opensearchClientResponse.hits.hits;
        const vizResponse = savedVisualizations.map((vizDocument) => ({
          label: vizDocument._source.visualization.title,
          key: vizDocument._id.split(':').pop(),
        }));
        return response.ok({
          body: { savedVisualizations: vizResponse },
        });
      } catch (error) {
        return response.custom({
          statusCode: error.statusCode || 500,
          body: error.message,
        });
      }
    }
  );
}
