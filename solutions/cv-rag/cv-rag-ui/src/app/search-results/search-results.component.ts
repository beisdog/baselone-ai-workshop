import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCard, MatCardContent, MatCardHeader, MatCardTitle } from '@angular/material/card';
import { MatChip } from '@angular/material/chips';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import { MatButton } from '@angular/material/button';
import { TextSegmentResult } from '../model/text-segment-result.model';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [
    CommonModule,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatChip,
    MatDivider,
    MatIcon,
    MatButton
  ],
  templateUrl: './search-results.component.html',
  styleUrls: ['./search-results.component.css']
})
export class SearchResultsComponent {
  @Input() searchResults: TextSegmentResult[] = [];
  @Input() loading: boolean = false;
  @Input() searchQuery: string = '';
  
  expandedStates: boolean[] = [];

  constructor() { }

  /**
   * Get metadata entries as key-value pairs for display
   */
  getMetadataEntries(metadata: {[key: string]: any} | Map<string, any> | any): [string, any][] {
    if (!metadata) return [];
    
    // Handle Map objects
    if (metadata instanceof Map) {
      return Array.from(metadata.entries());
    }
    
    // Handle plain objects
    if (typeof metadata === 'object') {
      return Object.entries(metadata);
    }
    
    return [];
  }

  /**
   * Format metadata value for display
   */
  formatMetadataValue(value: any): string {
    if (typeof value === 'object' && value !== null) {
      return JSON.stringify(value);
    }
    return String(value);
  }

  /**
   * Get display text with length limit
   */
  getTruncatedText(text: string, maxLength: number = 300): string {
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  }

  /**
   * Check if text is truncated
   */
  isTextTruncated(text: string, maxLength: number = 300): boolean {
    return text.length > maxLength;
  }

  /**
   * Toggle expanded state for a specific result
   */
  toggleExpanded(index: number): void {
    this.expandedStates[index] = !this.expandedStates[index];
  }

  /**
   * Track by function for ngFor performance
   */
  trackByIndex(index: number, item: TextSegmentResult): number {
    return index;
  }
}