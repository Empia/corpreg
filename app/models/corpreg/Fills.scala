package models
import models.daos._

import com.mohiva.play.silhouette.api.LoginInfo
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf


import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.daos.DelegableAuthInfoDAO
import com.mohiva.play.silhouette.impl.providers.OAuth1Info
import javax.inject.Inject
import play.api.libs.concurrent.Execution.Implicits._
import play.api.db.slick.DatabaseConfigProvider
import scala.concurrent.Future



case class FillDTO(id: Option[Long],
	phone:String,
	registered:Boolean = false,
	filled:Boolean = false,
	filledCorrect: Boolean = false,
	signRequested: Boolean = false,
	signMarked:Boolean = false,
	smsCode: String = "",
	signCompleted:Boolean = false)

trait FillsDAO {
  def create(fill:FillDTO): Future[Long] 
  def getAll:Future[Seq[FillDTO]]
  def registerUser(fillId: Long):Future[Boolean] 
  def areFilled(fillId: Long):Future[Boolean]   
  def correctFilling(fillId: Long):Future[Boolean] 
  def signRequested(fillId: Long):Future[Boolean] 
  def signMarketByCode(fillId: Long):Future[Boolean] 
  def smsCode(fillId: Long, code: String):Future[Boolean] 
  def signComplete(fillId: Long):Future[Boolean]

}
class FillsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends FillsDAO with DAOSlick {
	import driver.api._
  private def filterQuery(id: Long): Query[Fills, FillDTO, Seq] =
    fills.filter(_.id === id) 
  private def All(): Query[Fills, FillDTO, Seq] =
    fills

def getAll = db.run(All().result)
	// def createFilling
def create(fill:FillDTO): Future[Long] = {
	db.run(fills returning fills.map(_.id) += fill)
}
	// def registerUser
  def registerUser(fillId: Long):Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
  	fillOpt match {
  		case Some(fill) => {
  			db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id, registered = true))
  			).map { r =>
	  			true
  			}
  		}
  		case _ => {
  			Future.successful(false)
  		}
  	}
  }  
}
	// def correctFilling
  def areFilled(fillId: Long):Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
  	fillOpt match {
  		case Some(fill) => {
  			db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id, filled = true))
  			).map { r =>
	  			true
  			}
  		}
  		case _ => {
  			Future.successful(false)
  		}
  	}
  }  
 }
	// def correctFilling
  def correctFilling(fillId: Long):Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
  	fillOpt match {
  		case Some(fill) => {
  			db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id, filledCorrect = true))
  			).map { r =>
	  			true
  			}
  		}
  		case _ => {
  			Future.successful(false)
  		}
  	}
  }  
 }
	// def signRequested
  def signRequested(fillId: Long):Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
  	fillOpt match {
  		case Some(fill) => {
  			db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id, signRequested = true))
  			).map { r =>
	  			true
  			}
  		}
  		case _ => {
  			Future.successful(false)
  		}
  	}
  }  	
 }
	// def signMarketByCode
  def signMarketByCode(fillId: Long):Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
  	fillOpt match {
  		case Some(fill) => {
  			db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id, signMarked = true))
  			).map { r =>
	  			true
  			}
  		}
  		case _ => {
  			Future.successful(false)
  		}
  	}
  }
}    	

  def smsCode(fillId: Long, code: String):Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
  	fillOpt match {
  		case Some(fill) => {
  			db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id, smsCode = code))
  			).map { r =>
	  			true
  			}
  		}
  		case _ => {
  			Future.successful(false)
  		}
  	}
  }
}    	
	// def signComplete
  def signComplete(fillId: Long):Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
  	fillOpt match {
  		case Some(fill) => {
  			db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id, signCompleted = true))
  			).map { r =>
	  			true
  			}
  		}
  		case _ => {
  			    Future.successful(false)
  		}
  	}
  }
}    	
}

