package controllers

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers._
import forms.SignUpForm
import models.User
import models.services.UserService
import play.api.i18n.{ MessagesApi, Messages }
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action
import java.security.MessageDigest
import scala.concurrent._
import scala.concurrent.duration._
import scala.language.postfixOps

import play.api.mvc._
import play.api.libs.ws._
import scala.concurrent.ExecutionContext.Implicits.global


import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.ning._
import play.api.libs.ws._


class SMSComponent {//@Inject() (ws: WSClient) {
	val ws = NingWSClient() 
	val apiKey = "2737418e4610236d0c9f7dd373df59caa1cdacec"
	def getBalance() = {
		val login = "function31"

		val timestamp = getTimestamp()
		val signature = md5Hash(s"$login$timestamp$apiKey")

		val url = s"https://lk.redsms.ru/get/balance.php?login=$login&timestamp=$timestamp&signature=$signature"


		val futureResult: Future[String] = ws.url(url).get().map {
		  response =>
		    println(response.body)
			println("signature: " + signature)
			println("url: " + url)
		    response.body
		    //(response.json \ "person" \ "name").as[String]
		}
		Await.result(futureResult, 20 seconds)

	}



	def sendSms(to: String, text: String) = {
		val login = "function31"

		val timestamp = getTimestamp()
		val signature = generateSignature(login, 
				phone = to, 
				text = text, 
				sender = "Clerksy",
				timestamp = timestamp, 
				key = apiKey)

		val url = s"https://lk.redsms.ru/get/send.php?login=function31&signature=$signature&phone=$to&text=$text&sender=Clerksy&timestamp=$timestamp"


		val futureResult: Future[String] = ws.url(url).get().map {
		  response =>
		    println(response.body)
			println("signature: " + signature)
			println("url: " + url)
		    response.body
		    //(response.json \ "person" \ "name").as[String]
		}
		Await.result(futureResult, 20 seconds)
	}
	
	def getTimestamp():String = {
		// 
		// https://lk.redsms.ru/get/timestamp.php
		val futureResult: Future[String] = ws.url("https://lk.redsms.ru/get/timestamp.php").get().map { response =>
		 println(response.body)
		 response.body }
		Await.result(futureResult, 20 seconds)
	}
def generateSignature(login: String, 
	phone: String, 
	text: String, 
	sender: String,
	timestamp: String, 
	key: String = "2737418e4610236d0c9f7dd373df59caa1cdacec"):String = {

	val payload = s"$login$phone$sender$text$timestamp$key"
	// YourLogin79263000603Long text14567929882737418e4610236d0c9f7dd373df59caa1cdacec
	// function3179090860451test|??????????|2737418e4610236d0c9f7dd373df59caa1cdacec
	// function3179090860451test|1456793292|2737418e4610236d0c9f7dd373df59caa1cdacec

	println("payload "+payload)
	//val generated = MessageDigest.getInstance("MD5").digest(payload.getBytes)
	println("hash: "+ md5Hash(payload))
	//new String(generated)
	md5Hash(payload)
}

def md5Hash(text: String): String = java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
def md5Hash2(text: String) : String = java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
def generatePassword:String = {
	randomString(6)
}
def randomString(len: Int): String = {
    val rand = new scala.util.Random(System.nanoTime)
    val sb = new StringBuilder(len)
    val ab = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    for (i <- 0 until len) {
      sb.append(ab(rand.nextInt(ab.length)))
    }
    sb.toString
}

}