// Copyright 2015-2019 SWIM.AI inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package swim.io.http;

import org.testng.annotations.Ignore;
import swim.io.IpServiceRef;
import swim.io.IpSocketRef;
import java.io.IOException;
import java.net.ServerSocket;
import static org.testng.Assert.fail;

/*
  Tests here are currently failing and require further investigation.
  Ref:
    https://github.com/swimos/swim/issues/25
    https://github.com/swimos/swim/issues/22
 */
@Ignore
public class HttpSocketSpec extends HttpSocketBehaviors {
  final HttpSettings httpSettings = HttpSettings.standard();

  private int portNo = 0;

  HttpSocketSpec() {
    try {
      ServerSocket serverSocket = new ServerSocket(0);
      portNo = serverSocket.getLocalPort();
      serverSocket.setReuseAddress(true);
      serverSocket.close();
    } catch (IOException e) {
      fail("Failure in finding an available socket", e);
    }
  }

  @Override
  protected IpServiceRef bind(HttpEndpoint endpoint, HttpService service) {
    return endpoint.bindHttp("127.0.0.1", portNo, service, this.httpSettings);
  }

  @Override
  protected IpSocketRef connect(HttpEndpoint endpoint, HttpClient client) {
    return endpoint.connectHttp("127.0.0.1", portNo, client, this.httpSettings);
  }
}
