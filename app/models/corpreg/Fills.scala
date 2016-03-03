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
	// def fillFilling
	// def registerUser
	// def correctFilling
	// def signRequested
	// def signMarketByCode
	// def signComplete
}