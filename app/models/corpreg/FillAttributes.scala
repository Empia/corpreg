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



case class FillAttributeDTO(id: Option[Long],
	fill_id: Long,
	attribute:String,
	value:String)

trait FillAttributesDAO {

}
class FillAttributesDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) 
  extends FillAttributesDAO 
    with DAOSlick {
	    import driver.api._

}