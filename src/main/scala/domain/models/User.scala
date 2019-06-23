package domain.models

import infrastructure.mongodb.codecs.JodaDateTimeCodec
import org.joda.time.DateTime

case class User(
  id: String,
  name: String,
  email: String,
  username: String,
  password: String,
  updatedAt: DateTime,
  createdAt: DateTime,
  isActive: Boolean = false,
  defaultCompanyId: Option[String] = None
)

object User {
  import org.bson.codecs.configuration.CodecRegistries.{fromCodecs, fromProviders, fromRegistries}
  import org.bson.codecs.configuration.CodecRegistry
  import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
  import org.mongodb.scala.bson.codecs.Macros._

  implicit val userCodecRegistry: CodecRegistry = fromRegistries(
    fromProviders(classOf[User]),
    fromCodecs(new JodaDateTimeCodec),
    DEFAULT_CODEC_REGISTRY
  )
}