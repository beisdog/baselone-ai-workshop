import {Injectable} from '@angular/core';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {LlmModel} from '../model/llm-model.model';
import {lastValueFrom} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModelService {

  private apiUrl = environment.apiUrl; // Replace with your actual API URL

  constructor(private http: HttpClient) {
  }

  public getModels(): Promise<Array<LlmModel>> {
    return lastValueFrom(this.http.get<Array<LlmModel>>(this.apiUrl + '/llm/models'));
  }

}
