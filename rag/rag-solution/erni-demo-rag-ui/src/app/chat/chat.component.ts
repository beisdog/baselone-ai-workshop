import {AfterViewChecked, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatCard, MatCardActions, MatCardContent} from '@angular/material/card';
import {MatDivider} from '@angular/material/divider';
import {MatFormField, MatLabel} from '@angular/material/form-field';
import {MatIcon} from '@angular/material/icon';
import {NgClass, NgForOf, NgIf} from '@angular/common';
import {MatInput} from '@angular/material/input';
import {MatButton, MatIconButton} from '@angular/material/button';
import {AskService} from '../services/ask.service';
import {Message} from '../model/message.model';
import {MarkdownPipe} from '../pipe/markdown.pipe';
import {MatProgressSpinner} from '@angular/material/progress-spinner';
import {MatOption, MatSelect} from '@angular/material/select';
import {Profile} from '../model/profile.model';
import {ProfileService} from '../services/profile.service';
import {MatDialog} from '@angular/material/dialog';
import {MatCheckbox} from '@angular/material/checkbox';
import {finalize, Observable, of} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {LlmModel} from '../model/llm-model.model';
import {ModelService} from '../services/model.service';


@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  imports: [
    MatCard,
    MatCardContent,
    MatDivider,
    MatCardActions,
    MatFormField,
    MatLabel,
    MatIcon,
    NgClass,
    MatInput,
    MatIconButton,
    ReactiveFormsModule,
    NgForOf,
    MarkdownPipe,
    MatProgressSpinner,
    NgIf,
    MatSelect,
    MatOption,
    MatButton,
    MatCheckbox
  ],
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements AfterViewChecked, OnInit {

  chatForm: FormGroup;
  messages: Message[] = [];
  loading = false;
  @ViewChild('scrollContainer')
  chatContainer!: ElementRef;
  shouldScroll = false;
  profiles: Array<Profile> = [];
  llmModels: Array<LlmModel> = []
  vsNamespaces = ["PROFILE_FULL", "PROFILE_SUMMARY", "PROFILE_SKILLS", "PROFILE_PROJECTS"];

  constructor(private dialog: MatDialog,
              private fb: FormBuilder,
              private askService: AskService,
              private profileService: ProfileService,
              private modelService: ModelService) {
    this.chatForm = this.fb.group({
      userInput: ['', Validators.required],
      useHistory: [false],
      selectedProfile: null,
      selectedVSNamespace: null,
      selectedLlm: null,
      maxResults: [10],
      visibleVersion: [1]
    });
  }

  async ngOnInit(): Promise<void> {
    this.profiles = await this.profileService.getProfiles();
    this.llmModels = await this.modelService.getModels();
  }

  ngAfterViewChecked() {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }

  }

  ask() {
    if (this.chatForm.valid) {
      if (this.chatForm.value.useHistory) {
        this.askWithHistory();
      } else {
        this.askSimple();
      }
    }
  }

  askSimple() {
    if (this.chatForm.valid) {
      const question = this.chatForm.value.userInput;
      const userMessage: Message = {
        text: question,
        type: 'user'
      };
      this.messages.push(userMessage);
      this.loading = true
      const llm = this.chatForm.value.selectedLlm as string;
      this.executeServiceCall(this.askService.askQuestion(llm, question));
    }
  }

  askWithHistory() {
    debugger
    if (this.chatForm.valid) {
      const question = this.chatForm.value.userInput;
      const userMessage: Message = {
        text: question,
        type: 'user'
      };
      this.messages.push(userMessage);
      this.loading = true
      const llm = this.chatForm.value.selectedLlm as string;
      this.executeServiceCall(this.askService.askQuestionWithHistory(llm, this.messages));
    }
  }

  askAboutCV() {
    if (this.chatForm.valid) {
      const question = this.chatForm.value.userInput;
      const cvId = this.chatForm.value.selectedProfile;
      const userMessage: Message = {
        text: question,
        type: 'user'
      };
      this.messages.push(userMessage);
      this.executeServiceCall(this.askService.askAboutCV(cvId, question));
    }
  }

  askAboutCVList() {
    debugger;
    if (this.chatForm.valid) {
      const question = this.chatForm.value.userInput;
      const namespace = this.chatForm.value.selectedVSNamespace;
      const maxResults = this.chatForm.value.maxResults;
      const userMessage: Message = {
        text: question,
        type: 'user'
      };
      this.messages.push(userMessage);
      this.executeServiceCall(this.askService.askAboutCVList(namespace, question, maxResults));
    }
  }

  askAgent() {
    debugger;
    if (this.chatForm.valid) {
      const question = this.chatForm.value.userInput;
      const namespace = this.chatForm.value.selectedVSNamespace;
      const userMessage: Message = {
        text: question,
        type: 'user'
      };
      this.messages.push(userMessage);
      this.executeServiceCall(this.askService.askAgent(question));
    }
  }

  executeServiceCall(observable: Observable<any>) {
    this.loading = true
    observable.pipe(
      //tap(() => this.loading = true), // Ensure loading is set before request
      catchError(error => {
        console.error(error);
        return of(null); // Handle the error gracefully
      }),
      finalize(() => this.loading = false) // Ensures loading is turned off after completion
    ).subscribe(response => {
      if (response) {
        this.messages.push(response as Message);
      }
      this.shouldScroll = true;
    });
  }

  scrollToBottom() {
    const container = this.chatContainer.nativeElement;
    container.scrollTop = container.scrollHeight;
  }
}
