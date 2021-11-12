/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import { PluginInitializerContext } from '../../../src/core/server';
import { NotebooksPlugin } from './plugin';

//  This exports static code and TypeScript types,
//  as well as, OpenSearch Dashboards Platform `plugin()` initializer.

export function plugin(initializerContext: PluginInitializerContext) {
  return new NotebooksPlugin(initializerContext);
}

export { NotebooksPluginSetup, NotebooksPluginStart } from './types';
