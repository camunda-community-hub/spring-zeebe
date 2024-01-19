package io.camunda.console;

import io.camunda.common.auth.Authentication;
import io.camunda.console.client.model.BackupDto;
import io.camunda.console.client.model.ClusterClient;
import io.camunda.console.client.model.ClusterClientConnectionDetails;
import io.camunda.console.client.model.CreateCluster200Response;
import io.camunda.console.client.model.CreateClusterBody;
import io.camunda.console.client.model.CreateClusterClientBody;
import io.camunda.console.client.model.CreateSecretBody;
import io.camunda.console.client.model.CreatedClusterClient;
import io.camunda.console.client.model.IpWhiteListBody;
import io.camunda.console.client.model.PostMemberBody;
import io.camunda.console.impl.CamundaConsoleClientImpl;
import java.util.List;
import java.util.Map;

public interface CamundaConsoleClient {

  static CamundaConsoleClient create(Authentication authentication, String consoleUrl) {
    return new CamundaConsoleClientImpl(
        DefaultApiFactory.getInstance(authentication, consoleUrl).get());
  }

  Clusters clusters();

  Cluster clusters(String clusterId);

  Members members();

  Member members(String email);

  interface Clusters {
    List<io.camunda.console.client.model.Cluster> get();

    CreateCluster200Response post(CreateClusterBody request);

    io.camunda.console.client.model.Parameters parameters();

    interface Parameters {
      io.camunda.console.client.model.Parameters get();
    }
  }

  interface Cluster {
    io.camunda.console.client.model.Cluster get();

    void delete();

    Backups backups();

    Backup backups(String backupId);

    IpWhiteList ipwhitelist();

    Clients clients();

    Client clients(String clientId);

    Secrets secrets();

    Secret secrets(String secretName);

    interface Backups {
      List<BackupDto> get();

      BackupDto post();
    }

    interface Backup {
      BackupDto delete();
    }

    interface IpWhiteList {
      void put(IpWhiteListBody request);
    }

    interface Clients {
      List<ClusterClient> get();

      CreatedClusterClient post(CreateClusterClientBody request);
    }

    interface Client {
      ClusterClientConnectionDetails get();

      void delete();
    }

    interface Secrets {
      Map<String, String> get();

      void post(CreateSecretBody request);
    }

    interface Secret {
      void delete();
    }
  }

  interface Members {
    List<io.camunda.console.client.model.Member> get();
  }

  interface Member {
    void post(PostMemberBody request);

    void delete();
  }
}
