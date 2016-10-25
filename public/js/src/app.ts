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


	 this.class = {okveds: [{code: "01.1", title:"Выращивание однолетних культур" }], hidden: false};   


this.allOkveds = [
{okveds: [{code: "01.1", title:"Выращивание однолетних культур" }], 
childs: [{code: "01.11", title: "01.11 код", childs: [{code: "01.11.11", title: "01.11.11"}], }], 
hidden: false},
{okveds:[{code: "01.2", title: "Выращивание многолетних культур"}],
childs: [{code: "01.21", title: "01.11 код", childs: [{code: "01.11.11", title: "01.11.11"}], }], 
hidden: true},
{okveds:[{code: "01.3", title: "Выращивание рассады"}],
childs: [{code: "01.31", title: "01.11 код", childs: [{code: "01.11.11", title: "01.11.11"}], }], 
hidden: true},
{okveds:[{code: "01.4", title: "Животноводство"}],
childs: [{code: "01.41", title: "01.11 код", childs: [{code: "01.11.11", title: "01.11.11"}], }], 
hidden: true},
{okveds:[{code: "01.5", title: "Смешанное сельское хозяйство"}],
childs: [{code: "01.51", title: "01.11 код", childs: [{code: "01.11.11", title: "01.11.11"}], }], 
hidden: true},
{okveds:[{code: "01.6", title: "Деятельность вспомогательная в области производства сельскохозяйственных культур и послеуборочной обработки сельхозпродукции"}],
childs: [{code: "01.61", title: "01.11 код", childs: [{code: "01.11.11", title: "01.11.11"}], }], 
hidden: true},
{okveds:[{code: "01.7", title: "Охота, отлов и отстрел диких животных, включая предоставление услуг в этих областях"
}],
childs: [{code: "01.11", title: "01.11 код", childs: [{code: "01.11.11", title: "n nn n01.11.11"}], }], 
hidden: true}
];

	 this.class2 = {okveds: [{code: "01.2", title:"Тестовый оквэд 01.2" }], hidden: true};   
	 this.class3 = {okveds: [{code: "01.3", title:"Тестовый оквэд 01.3" }], hidden: true};   
	 this.class4 = {okveds: [{code: "01.4", title:"Тестовый оквэд 01.4" }], hidden: true};   
	 this.class5 = {okveds: [{code: "01.5", title:"Тестовый оквэд 01.5" }], hidden: true};   
	 this.class6 = {okveds: [{code: "01.6", title:"Тестовый оквэд 01.6" }], hidden: true};   
	 this.class7 = {okveds: [{code: "01.7", title:"Тестовый оквэд 01.7" }], hidden: true};   

this.isExistedCode = function(code) {
	return (this.selectedOkved.find(function(d){return d.code === code}) !== undefined)
}

this.addToSelected = function(okved) {
	console.log('this.addToSelected = function(okved) {', okved);
	if (!this.isExistedCode(okved.code) ) {
		this.selectedOkved.push(okved);
	}
}


this.addChildToSelected = function(okvedParent) {
	console.log('this.addChildToSelected = function(okvedParent) {', okvedParent);
	if (!this.isExistedCode(okvedParent.code) ) {
		this.selectedOkved.push(okvedParent);
	}
}

this.isOkvedSelected = function(okved) {
	return this.selectedOkved.filter(function(el) {
	  return el.code == okved.code;
	}).length > 0
}

this.primaryOkved = null;

this.isPrimaryOkved = function(okved) {
	if (this.primaryOkved !== null && this.primaryOkved.code == okved.code) {
	 return true;
	} else {
	  return false;
	}
}

this.asPrimaryOkved = function(okved) {
	if (this.primaryOkved == null || this.primaryOkved.code !== okved.code) {
	  this.primaryOkved = okved;
	} else {
	  this.primaryOkved = null;
	}
	console.log('primaryOkved',this.primaryOkved)
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