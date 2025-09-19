import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {Message} from '../model/message.model';

@Injectable({
  providedIn: 'root'
})
export class AskService {

  private apiUrl = environment.apiUrl; // Replace with your actual API URL

  constructor(private http: HttpClient) {
  }

  askQuestion(question: string): Observable<any> {
    const body = {question: question};
    return this.http.post<any>(this.apiUrl + '/llm/ask/simple', body);
  }

  askQuestionWithHistory(messages: Message[]): Observable<any> {
    const body = messages;
    return this.http.post<any>(this.apiUrl + '/llm/ask/messages', body);
  }

  askAboutCV(id: string, question: string): Observable<any> {
    const body = {question: question};
    return this.http.post<any>(this.apiUrl + "/cv/ask/cv/" + id, body);
  }

  askAboutCVList(namespace: string, question: string, maxResults: number): Observable<any> {
    const body = {question: question, maxResults: maxResults};
    return this.http.post<any>(this.apiUrl + "/cv/ask/cv-list/" + namespace, body);
  }

  askAgent(question: string): Observable<any> {
    const body = {question: question};
    return this.http.post<any>(this.apiUrl + "/cv/agent", body);
  }
}
