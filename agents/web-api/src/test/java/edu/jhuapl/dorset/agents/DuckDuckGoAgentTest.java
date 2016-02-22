/*
 * Copyright 2016 The Johns Hopkins University Applied Physics Laboratory LLC
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.jhuapl.dorset.agents;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import edu.jhuapl.dorset.agent.Agent;
import edu.jhuapl.dorset.agent.AgentMessages;
import edu.jhuapl.dorset.agent.AgentRequest;
import edu.jhuapl.dorset.agent.AgentResponse;
import edu.jhuapl.dorset.http.HttpClient;

public class DuckDuckGoAgentTest {

    @Test
    public void testGetGoodResponse() {
        String query = "Barack Obama";
        String jsonData = FileReader.getFileAsString("duckduckgo/barack_obama.json");
        HttpClient client = mock(HttpClient.class);
        when(client.get(DuckDuckGoAgent.createUrl(query))).thenReturn(jsonData);

        Agent agent = new DuckDuckGoAgent(client);
        AgentResponse response = agent.process(new AgentRequest(query));

        assertEquals(AgentMessages.SUCCESS, response.getStatusCode());
        assertTrue(response.getText().startsWith("Barack Hussein Obama II is an American politician"));
    }

    @Test
    public void testWithFullSentence() {
        String jsonData = FileReader.getFileAsString("duckduckgo/barack_obama.json");
        HttpClient client = mock(HttpClient.class);
        when(client.get(DuckDuckGoAgent.createUrl("Barack Obama"))).thenReturn(jsonData);

        Agent agent = new DuckDuckGoAgent(client);
        AgentResponse response = agent.process(new AgentRequest("Who is Barack Obama?"));

        assertEquals(AgentMessages.SUCCESS, response.getStatusCode());
        assertTrue(response.getText().startsWith("Barack Hussein Obama II is an American politician"));
    }

    @Test
    public void testGetDisambiguationResponse() {
        String query = "Obama";
        String jsonData = FileReader.getFileAsString("duckduckgo/obama.json");
        HttpClient client = mock(HttpClient.class);
        when(client.get(DuckDuckGoAgent.createUrl(query))).thenReturn(jsonData);

        Agent agent = new DuckDuckGoAgent(client);
        AgentResponse response = agent.process(new AgentRequest(query));

        assertEquals(AgentMessages.MORE_INFORMATION_NEEDED, response.getStatusCode());
    }

    @Test
    public void testGetEmptyResponse() {
        String query = "zergblah";
        String jsonData = FileReader.getFileAsString("duckduckgo/zergblah.json");
        HttpClient client = mock(HttpClient.class);
        when(client.get(DuckDuckGoAgent.createUrl(query))).thenReturn(jsonData);

        Agent agent = new DuckDuckGoAgent(client);
        AgentResponse response = agent.process(new AgentRequest(query));

        assertEquals(AgentMessages.UNKNOWN_ANSWER, response.getStatusCode());
    }

    @Test
    public void testUrlEncoding() {
        String urlBase = "http://api.duckduckgo.com/?format=json&q=";
        assertEquals(urlBase + "Barack+Obama", DuckDuckGoAgent.createUrl("Barack Obama"));
    }

}