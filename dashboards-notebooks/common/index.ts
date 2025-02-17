/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

import { RefObject } from 'react';

export const PLUGIN_ID = 'notebooks-dashboards';
export const PLUGIN_NAME = 'Notebooks';
export const API_PREFIX = '/api/notebooks';
export const SELECTED_BACKEND = 'DEFAULT'; // ZEPPELIN || DEFAULT
export const DATE_FORMAT = 'MM/DD/YYYY hh:mm A';
export const FETCH_SIZE = 1000;
export const CREATE_NOTE_MESSAGE = 'Enter a name to describe the purpose of this notebook.';
export const DOCUMENTATION_URL = 'https://opensearch.org/docs/dashboards/notebooks/';

export const zeppelinURL = 'http://localhost:8080';

export const wreckOptions = {
  baseUrl: zeppelinURL,
  headers: { 'Content-Type': 'application/json' },
};

const BASE_NOTEBOOKS_URI = '/_plugins/_notebooks';
export const OPENSEARCH_NOTEBOOKS_API = {
  GET_NOTEBOOKS: `${BASE_NOTEBOOKS_URI}/notebooks`,
  NOTEBOOK: `${BASE_NOTEBOOKS_URI}/notebook`,
};

export interface optionsType {
  baseUrl: string;
  payload?: any;
  headers?: any;
  redirects?: number;
  beforeRedirect?: any;
  redirected?: any;
  timeout?: number; // default: unlimited
  maxBytes?: number; // default: unlimited
  rejectUnauthorized?: boolean;
  secureProtocol?: string; // The SSL method to use
  ciphers?: string; // The TLS ciphers to support
}

export type ParaType = {
  uniqueId: string;
  isRunning: boolean;
  inQueue: boolean;
  isSelected: boolean;
  isInputHidden: boolean;
  isOutputHidden: boolean;
  showAddPara: boolean;
  isVizualisation: boolean;
  vizObjectInput: string;
  id: number;
  inp: string;
  lang: string;
  editorLanguage: string;
  typeOut: Array<string>;
  out: any[];
  isInputExpanded: boolean;
  isOutputStale: boolean;
  paraRef: RefObject<React.ReactElement>;
  paraDivRef: RefObject<HTMLDivElement>;
  visStartTime?: string;
  visEndTime?: string;
  visSavedObjId?: string;
};
