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

	 this.class = {hidden: false};   

	 this.class2 = {hidden: true};   
	 this.class3 = {hidden: true};   
	 this.class4 = {hidden: true};   
	 this.class5 = {hidden: true};   
	 this.class6 = {hidden: true};   
	 this.class7 = {hidden: true};   


  }
}

@NgModule({
  imports: [ BrowserModule ],
  declarations: [ App ],
  bootstrap: [ App ]
})
export class AppModule {
	
}