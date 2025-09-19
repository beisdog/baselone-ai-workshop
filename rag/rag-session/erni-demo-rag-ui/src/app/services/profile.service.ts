import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {lastValueFrom} from 'rxjs';
import {environment} from '../../environments/environment';
import {Profile} from '../model/profile.model';

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private apiUrl = environment.apiUrl + '/cv'; // Replace with your actual API URL

  constructor(private http: HttpClient) {}

  getProfiles(): Promise<Array<Profile>> {
    return lastValueFrom(this.http.get<Array<Profile>>(this.apiUrl + "/profiles"));
  }
}
