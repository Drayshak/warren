package engineer.carrot.warren.warren.handler.rpl

import engineer.carrot.warren.kale.irc.message.rpl.Rpl332Message
import engineer.carrot.warren.kale.irc.message.utility.CaseMapping
import engineer.carrot.warren.warren.state.*
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class Rpl332HandlerTests {

    lateinit var handler: Rpl332Handler
    lateinit var channelsState: ChannelsState
    val caseMappingState = CaseMappingState(mapping = CaseMapping.RFC1459)

    @Before fun setUp() {
        channelsState = emptyChannelsState(caseMappingState)
        handler = Rpl332Handler(channelsState.joined, caseMappingState)
    }

    @Test fun test_handle_NonexistentChannel_DoesNothing() {
        channelsState.joined += ChannelState(name = "#channel", users = generateUsers("test-nick", mappingState = caseMappingState))

        handler.handle(Rpl332Message(source = "", target = "", channel = "#somewhere", topic = "test topic"), mapOf())

        val expectedChannelState = ChannelState(name = "#channel", users = generateUsers("test-nick", mappingState = caseMappingState))

        assertEquals(channelsStateWith(listOf(expectedChannelState), caseMappingState), channelsState)
    }

    @Test fun test_handle_ValidChannel_SetsTopic() {
        channelsState.joined += ChannelState(name = "#channel", users = generateUsers("test-nick", mappingState = caseMappingState))

        handler.handle(Rpl332Message(source = "", target = "", channel = "#channel", topic = "test topic"), mapOf())

        val expectedChannelState = ChannelState(name = "#channel", users = generateUsers("test-nick", mappingState = caseMappingState), topic = "test topic")

        assertEquals(channelsStateWith(listOf(expectedChannelState), caseMappingState), channelsState)
    }

}