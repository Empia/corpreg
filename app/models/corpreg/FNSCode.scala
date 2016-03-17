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






trait FNSCodeDAO {
  def getAll:Future[Seq[FNSCodeDTO]]
  def getByGlobal(glob: String):Future[Seq[FNSCodeDTO]]
}
class FNSCodeDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends FNSCodeDAO with DAOSlick {
	import driver.api._
  private def filterQueryByGlobal(glob: String): Query[FNSCodes, FNSCodeDTO, Seq] =
    codes5.filter(_.id === glob)
  private def All(): Query[FNSCodes, FNSCodeDTO, Seq] =
    codes5

def getAll = db.run(All().result)
def getByGlobal(glob: String) = db.run(filterQueryByGlobal(glob).result)


}
