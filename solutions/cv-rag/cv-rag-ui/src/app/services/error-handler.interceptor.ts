import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { MatDialog } from '@angular/material/dialog';
import { ErrorDialogComponent } from '../error-dialog/error-dialog.component';

@Injectable()
export class ErrorHandlerInterceptor implements HttpInterceptor {

  constructor(private dialog: MatDialog) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        console.error("Interceptor caught error:",error);
        this.showErrorDialog(error);
        return throwError(() => error);
      })
    );
  }

  private showErrorDialog(error: HttpErrorResponse): void {
    this.dialog.open(ErrorDialogComponent, {
      width: '400px',
      data: {
        title: 'Error',
        message: error.message || 'An unexpected error occurred!',
        status: error.status
      }
    });
  }
}
