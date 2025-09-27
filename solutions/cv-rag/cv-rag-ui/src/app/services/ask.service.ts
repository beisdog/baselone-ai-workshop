import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Message} from '../model/message.model';
import {TextSegmentResult} from '../model/text-segment-result.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AskService {

  private apiUrl = environment.apiUrl; // Replace with your actual API URL

  constructor(private http: HttpClient) {
  }

  askQuestion(model: string, question: string): Observable<Message> {
    const body = {
      model: model,
      question: question};
    return this.http.post<Message>(this.apiUrl + '/llm/ask/simple', body);
  }

  askQuestionWithHistory(model: string, messages: Message[]): Observable<Message> {
    const body = {
      model: model,
      messages: messages
    };
    return this.http.post<Message>(this.apiUrl + '/llm/ask/messages', body);
  }

  askAboutCV(id: string, question: string): Observable<Message> {
    const body = {question: question};
    return this.http.post<Message>(this.apiUrl + "/cv/ask/cv/" + id, body);
  }

  askAboutCVList(namespace: string, question: string, maxResults: number): Observable<Message> {
    const body = {question: question, maxResults: maxResults};
    return this.http.post<Message>(this.apiUrl + "/cv/ask/cv-list/" + namespace, body);
  }

  vectorSearch(namespace: string, question: string, maxResults: number): Observable<Array<TextSegmentResult>> {
    const body = {question: question, maxResults: maxResults};
    return this.http.post<Array<TextSegmentResult>>(this.apiUrl + "/vs/search/" + namespace, body);
  }

  askAgent(question: string): Observable<any> {
    const body = {question: question};
    return this.http.post<any>(this.apiUrl + "/cv/agent", body);
  }
}
