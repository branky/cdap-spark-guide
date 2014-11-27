/*
 * Copyright © 2014 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package co.cask.cdap.guides;

import co.cask.cdap.api.annotation.UseDataSet;
import co.cask.cdap.api.dataset.lib.ObjectStore;
import co.cask.cdap.api.service.http.AbstractHttpServiceHandler;
import co.cask.cdap.api.service.http.HttpServiceRequest;
import co.cask.cdap.api.service.http.HttpServiceResponder;
import com.google.common.base.Charsets;

import java.net.HttpURLConnection;
import java.nio.ByteBuffer;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Handles queries retrieving the PageRank of a URL.
 */
public class PageRankHandler extends AbstractHttpServiceHandler {

  @UseDataSet("pageRanks")
  private ObjectStore<Double> pageRanks;

  @Path("pagerank")
  @POST
  public void handleBackLink(HttpServiceRequest request, HttpServiceResponder responder) {

    ByteBuffer requestContents = request.getContent();
    if (requestContents == null) {
      responder.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "No URL provided.");
      return;
    }

    String urlParam = Charsets.UTF_8.decode(requestContents).toString();

    Double rank = pageRanks.read(urlParam);
    if (rank == null) {
      responder.sendError(HttpURLConnection.HTTP_BAD_REQUEST, "The following URL was not found: " + urlParam);
      return;
    }

    responder.sendJson(String.valueOf(rank));
  }
}
