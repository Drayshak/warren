package engineer.carrot.warren.warren.handler

import engineer.carrot.warren.kale.IKaleHandler
import engineer.carrot.warren.kale.irc.message.ircv3.CapLsMessage
import engineer.carrot.warren.kale.irc.message.ircv3.CapReqMessage
import engineer.carrot.warren.warren.IMessageSink
import engineer.carrot.warren.warren.extension.cap.CapLifecycle
import engineer.carrot.warren.warren.extension.cap.CapState
import engineer.carrot.warren.warren.extension.sasl.SaslState
import engineer.carrot.warren.warren.handler.helper.RegistrationHelper
import engineer.carrot.warren.warren.loggerFor

class CapLsHandler(val capState: CapState, val saslState: SaslState, val sink: IMessageSink) : IKaleHandler<CapLsMessage> {

    private val LOGGER = loggerFor<CapLsHandler>()

    override val messageType = CapLsMessage::class.java

    override fun handle(message: CapLsMessage, tags: Map<String, String?>) {
        val caps = message.caps
        val lifecycle = capState.lifecycle

        capState.server += message.caps

        LOGGER.trace("server supports following caps: $caps")

        when (lifecycle) {
            CapLifecycle.NEGOTIATING -> {
                if (!message.isMultiline) {
                    val requestCaps = capState.server.keys.intersect(capState.negotiate)
                    val implicitlyRejectedCaps = capState.negotiate.subtract(requestCaps)

                    capState.rejected += implicitlyRejectedCaps

                    if (RegistrationHelper.shouldEndCapNegotiation(saslState, capState)) {
                        LOGGER.trace("server gave us caps and ended with a non-multiline ls, not in the middle of SASL auth, implicitly rejecting: $implicitlyRejectedCaps, nothing left so ending negotiation")

                        RegistrationHelper.endCapNegotiation(sink, capState)
                    } else if (!requestCaps.isEmpty()) {
                        LOGGER.trace("server gave us caps and ended with a non-multiline ls, requesting: $requestCaps, implicitly rejecting: $implicitlyRejectedCaps")

                        sink.write(CapReqMessage(caps = requestCaps.distinct()))
                    }
                } else {
                    LOGGER.trace("server gave us a multiline cap ls, expecting more caps before ending")
                }
            }

            else -> LOGGER.trace("server told us about caps but we don't think we're negotiating")
        }
    }

}

