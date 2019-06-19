package company_admin

import authentication.AuthenticatedUser
import com.google.inject.{Inject, Singleton}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.inject.Logging
import company_admin.requests.SingleResourceRequest
import domain.models.{Company, UnitOfMeasurement}
import domain.repositories.UnitOfMeasurementRepository

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class UnitOfMeasurementController @Inject()(authenticatedUser: AuthenticatedUser,
                                            repository: UnitOfMeasurementRepository) extends Controller with Logging  {
  private val API_VERSION = "v1"
  private val COMPANY_SLUG = "company_slug"
  private val BASE_RESOURCE: String = "/" + API_VERSION + "/:" + COMPANY_SLUG

  get(BASE_RESOURCE + "/units_of_measurement") { _: Request =>
    authenticatedUser
      .getCompany
      .unitsOfMeasurement
      .sortBy(_.createdAt.get.getMillis)
      .reverse
  }

  post(BASE_RESOURCE + "/units_of_measurement") { payload: UnitOfMeasurement =>
    implicit val company: Company = authenticatedUser.getCompany
    info(s"Saving company $company")

    repository
      .create(payload)
      .map(unitOfMeasurement => response.created.body(unitOfMeasurement))
  }

  put(BASE_RESOURCE + "/unit_of_measurement/:id") { payload: UnitOfMeasurement =>
    implicit val company: Company = authenticatedUser.getCompany
    payload.id.map { unitOfMeasurementId =>
      repository
        .update(unitOfMeasurementId, payload)
        .map { _ => payload}
    }
  }

  get(BASE_RESOURCE + "/unit_of_measurement/:id") { request: SingleResourceRequest =>
    authenticatedUser
      .getCompany
      .unitsOfMeasurement
      .find(_.id.exists(_.equals(request.id)))
  }

  delete(BASE_RESOURCE + "/unit_of_measurement/:id") { request: SingleResourceRequest =>
    implicit val company: Company = authenticatedUser.getCompany

    repository
      .delete(request.id)
      .map { _ => response.noContent}
  }
}
