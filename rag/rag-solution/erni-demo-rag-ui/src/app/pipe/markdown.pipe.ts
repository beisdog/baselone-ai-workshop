import { Pipe, PipeTransform } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import showdown from 'showdown';

@Pipe({
  name: 'markdown'
})
export class MarkdownPipe implements PipeTransform {
  private converter = new showdown.Converter({
    tables: true,
    simplifiedAutoLink: true,
    strikethrough: true,
    tasklists: true
  });

  constructor(private sanitizer: DomSanitizer) {}

  transform(value: string): SafeHtml {
    const html = this.converter.makeHtml(value);
    return this.sanitizer.bypassSecurityTrustHtml(html);
  }
}
