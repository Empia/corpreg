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


trait FormPositionsDAO {
	 def createPosition(position: FormPosition):Future[Boolean]
	 def getPosition(page: String):Future[Seq[FormPosition]]
	 def updatePosition(position: FormPosition):Future[Boolean]
}
class FormPositionsDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends FormPositionsDAO
    with DAOSlick {
	    import driver.api._

			private def filterQuery(page: String): Query[FormPositions, FormPosition, Seq] =
				form_positions.filter(_.page === page)

				private def filterQuery2(page: String, id: String):Query[FormPositions, FormPosition, Seq] = {
					form_positions.filter(c => c.page === page && c.id === id)
				}

 def getPosition(page: String) = {
	 db.run(filterQuery(page).result)
}

def createPosition(position: FormPosition):Future[Boolean] = {
	db.run(form_positions returning form_positions.map(_.id) += position).map { r =>
		true
	}
}

def updatePosition(position: FormPosition):Future[Boolean] = {
	db.run(filterQuery2(position.page, position.id).update(position)).map { r =>
		true
	}
}

}

trait FillAttributesDAO {
	def findOrCreate(fillId: Long, fillAttr:FillAttributeDTO):Future[Boolean]
	def findByFill(fillId: Long):Future[Seq[FillAttributeDTO]]
}

class FillAttributesDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)
  extends FillAttributesDAO
    with DAOSlick {
	    import driver.api._
  private def filterQuery(id: Long): Query[FillAttributes, FillAttributeDTO, Seq] =
    fill_attributes.filter(_.id === id)

  private def filterQueryByFillId(id: Long): Query[FillAttributes, FillAttributeDTO, Seq] =
    fill_attributes.filter(_.fill_id === id)

  private def filterQueryByFillAndAttribute(fillId: Long, attribute: String): Query[FillAttributes, FillAttributeDTO, Seq] =
    fill_attributes.filter(at => at.fill_id === fillId && at.attribute === attribute)

  private def All(): Query[FillAttributes, FillAttributeDTO, Seq] =
    fill_attributes

  def findByFill(fillId: Long):Future[Seq[FillAttributeDTO]] = {
  	db.run(filterQueryByFillId(fillId).result)
  }
  def findOrCreate(fillId: Long, fillAttr:FillAttributeDTO):Future[Boolean] = {
  db.run(filterQueryByFillAndAttribute(fillId, fillAttr.attribute).result.headOption).flatMap { fillAttrOpt =>
  	fillAttrOpt match {
  		case Some(attr) => {
  			db.run(fill_attributes.filter(_.id === attr.id.get).update(fillAttr.copy(id = attr.id))
  			).map { r =>
	  			true
  			}
  		}
  		case _ => {
  			db.run(fill_attributes returning fill_attributes.map(_.id) += fillAttr).map { r =>
  				true
  			}
  		}
  	}
  }
  	// .update(bpToUpdate)
  }

}
