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
    this.selectedOkved = [];


	 this.class = {okveds: [{code: "01.1", title:"Тестовый оквэд 01.1" }], hidden: false};   

	 this.class2 = {okveds: [{code: "01.2", title:"Тестовый оквэд 01.2" }], hidden: true};   
	 this.class3 = {okveds: [{code: "01.3", title:"Тестовый оквэд 01.3" }], hidden: true};   
	 this.class4 = {okveds: [{code: "01.4", title:"Тестовый оквэд 01.4" }], hidden: true};   
	 this.class5 = {okveds: [{code: "01.5", title:"Тестовый оквэд 01.5" }], hidden: true};   
	 this.class6 = {okveds: [{code: "01.6", title:"Тестовый оквэд 01.6" }], hidden: true};   
	 this.class7 = {okveds: [{code: "01.7", title:"Тестовый оквэд 01.7" }], hidden: true};   

this.addToSelected = function(okved) {
	this.selectedOkved.push(okved);
}
this.removeFromSelected = function(okved) {
	this.selectedOkved = this.selectedOkved.filter(function(el) {
	    return el.code !== okved.code;
	});
}


  }
}

@NgModule({
  imports: [ BrowserModule ],
  declarations: [ App ],
  bootstrap: [ App ]
})
export class AppModule {
	
}