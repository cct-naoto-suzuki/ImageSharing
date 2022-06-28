package play

import play.api.mvc.Action

trait RestAction[Req, Res] extends Action[Req]
