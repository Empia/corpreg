//our root app component
import {Component, NgModule} from '@angular/core'
import {BrowserModule} from '@angular/platform-browser'

@Component({
  selector: 'my-app',
  templateUrl: './assets/app.html' 
})
export class App {
  name:string;
  constructor() {
    this.name = 'Angular2'
    this.selectedOkved = []

    
  }
}

@NgModule({
  imports: [ BrowserModule ],
  declarations: [ App ],
  bootstrap: [ App ]
})
export class AppModule {
	
}