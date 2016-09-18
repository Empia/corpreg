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
	signCompleted:Boolean = false,
  addressFilled: Boolean = false,
  nalogFilled: Boolean = false,
  userFilesUploaded: Boolean = false,
  signCreationConfirm: Boolean = false,
  identityConfirmRequest:Boolean = false,
  identityConfirmApproved:Boolean = false)

trait FillsDAO {
  def create(fill:FillDTO): Future[Long] 
  def delete(fillId:Long): Future[Int] 

  def getAll:Future[Seq[FillDTO]]
  def getByPhone(phone: String):Future[Option[FillDTO]]  
  def registerUser(fillId: Long):Future[Boolean] 
  def areFilled(fillId: Long):Future[Boolean]   
  def correctFilling(fillId: Long):Future[Boolean] 
  def signRequested(fillId: Long):Future[Boolean] 
  def signMarketByCode(fillId: Long):Future[Boolean] 
  def smsCode(fillId: Long, code: String):Future[Boolean] 
  def signComplete(fillId: Long):Future[Boolean]


def setAttraddressFilled(fillId: Long): Future[Boolean]
def setAttrnalogFilled(fillId: Long): Future[Boolean]
def setAttruserFilesUploaded(fillId: Long): Future[Boolean]
def setAttrsignCreationConfirm(fillId: Long): Future[Boolean]
def setAttridentityConfirmRequest(fillId: Long): Future[Boolean]
def setAttridentityConfirmApproved(fillId: Long): Future[Boolean]

}
class FillsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends FillsDAO with DAOSlick {
	import driver.api._
  private def filterQuery(id: Long): Query[Fills, FillDTO, Seq] =
    fills.filter(_.id === id) 
  private def filterQueryByPhone(phone: String): Query[Fills, FillDTO, Seq] =
    fills.filter(_.phone === phone)     
  private def All(): Query[Fills, FillDTO, Seq] =
    fills

def getByPhone(phone: String):Future[Option[FillDTO]] = db.run(filterQueryByPhone(phone).result.headOption)

def delete(fillId:Long): Future[Int] = {
	db.run(filterQuery(fillId).delete)
}


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


def setAttraddressFilled(fillId: Long): Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
    fillOpt match {
      case Some(fill) => {
        db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id,  addressFilled = true))
          ).map { r =>
          true
        }
      }
      case _ => Future.successful(false)
    }
  }
}
def setAttrnalogFilled(fillId: Long): Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
    fillOpt match {
      case Some(fill) => {
        db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id,  nalogFilled = true))
          ).map { r =>
          true
        }
      }
      case _ => Future.successful(false)
    }
  }
}
def setAttruserFilesUploaded(fillId: Long): Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
    fillOpt match {
      case Some(fill) => {
        db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id,  userFilesUploaded = true))
          ).map { r =>
          true
        }
      }
      case _ => Future.successful(false)
    }
  }
}
def setAttrsignCreationConfirm(fillId: Long): Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
    fillOpt match {
      case Some(fill) => {
        db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id,  signCreationConfirm = true))
          ).map { r =>
          true
        }
      }
      case _ => Future.successful(false)
    }
  }
}
def setAttridentityConfirmRequest(fillId: Long): Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
    fillOpt match {
      case Some(fill) => {
        db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id, identityConfirmRequest = true ))
          ).map { r =>
          true
        }
      }
      case _ => Future.successful(false)
    }
  }
}
def setAttridentityConfirmApproved(fillId: Long): Future[Boolean] = {
  db.run(filterQuery(fillId).result.headOption).flatMap { fillOpt =>
    fillOpt match {
      case Some(fill) => {
        db.run(fills.filter(_.id === fill.id.get).update(fill.copy(id = fill.id, identityConfirmApproved = true ))
          ).map { r =>
          true
        }
      }
      case _ => Future.successful(false)
    }
  }
}

}

