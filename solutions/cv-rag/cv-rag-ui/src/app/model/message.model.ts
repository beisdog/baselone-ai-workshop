import {TextSegmentResult} from './text-segment-result.model';

export interface Message {
  text: string;
  type: 'user' | 'assistant'| 'system';
  searchResults?: Array<TextSegmentResult>;
}
