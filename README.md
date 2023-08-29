# Implementing OAuth 2.0 Client Credentials Flow with AWS Cognito for Secure Server-to-Server Communication

In today's interconnected digital landscape, secure communication between servers is paramount. 
As organizations continue to expand their digital footprint, the need for robust authentication and authorization mechanisms becomes more critical than ever. 
This is where OAuth 2.0 steps in—a powerful protocol that enforces and facilitates secure access to resources on behalf of users or applications, without exposing sensitive credentials.

In the realm of server-to-server communication, the OAuth 2.0 Client Credentials Flow emerges as a reliable solution. 
This flow enables servers to securely communicate with one another, requesting and exchanging data without user involvement. 
It's a practical scenario that finds applications in microservices architectures, API-driven interactions, and various other server-based interactions.

To delve into the real-world implementation of the OAuth 2.0 Client Credentials Flow, we turn to Amazon Web Services (AWS) Cognito — the authentication and authorization service that provides scalable user identity management. 
Leveraging AWS Cognito as our Authorization Server, we'll demonstrate how to set up a seamless and secure server-to-server communication channel.

Throughout this article, we'll guide you through the configuration steps required within AWS Cognito to establish this communication paradigm. 
But we won't stop there. We recognize that theory only goes so far. 
To truly grasp the implementation process, we'll provide you with a hands-on experience. 
We'll walk you through the creation of two Java Web applications, each fortified with a robust security layer to ensure authorized interactions.

By the end of this article, you'll not only comprehend the concepts and benefits of OAuth 2.0 Client Credentials Flow but also possess the practical knowledge to implement it effectively using AWS Cognito. 
So, whether you're an application developer, a system architect, or an enthusiast eager to enhance your understanding of secure server communications, this article aims to equip you with the insights and skills you need.

Let's embark on a journey to establish a secure server-to-server communication channel—powered by the OAuth 2.0 Client Credentials Flow and AWS Cognito. 
Through a blend of theory and practical implementation, you'll soon find yourself well-versed in orchestrating secure interactions in the dynamic landscape of modern server communication.

## Understanding OAuth 2.0

Before we dive into the practical implementation using AWS Cognito, let's lay the foundation by understanding the core concepts and benefits of the OAuth 2.0 protocol. 
OAuth 2.0, often referred to simply as OAuth, is an industry-standard authorization protocol that enables secure access to resources by allowing applications to obtain limited access to a user's account on behalf of that user.

Key OAuth 2.0 concepts include:
1. Clients and Resources: In the context of OAuth 2.0, a client is an application seeking access to protected resources, and a resource server hosts these protected resources. These resources could be anything from user data to APIs, and the client's goal is to access these resources without exposing sensitive credentials.
2. Authorization Server: The authorization server is responsible for authenticating the client and user, as well as issuing access tokens after the user grants consent. These access tokens are then used by the client to access the protected resources on the resource server.
3. Access Tokens: Access tokens are the centerpiece of OAuth 2.0. They are short-lived credentials that a client application uses to access resources on behalf of the user. Access tokens are specific to a particular scope (what the token allows access to) and duration (how long the token is valid).
4. Grant Types: OAuth 2.0 defines several grant types, each designed to address different use cases and security requirements. These grant types include:
- Authorization Code Grant: Used in web applications where the client can securely maintain a client secret and user is directly involved in the authorization process. It involves a multi-step process that involves user redirection and authorization codes exchange.
- Implicit Grant: Primarily used for single-page applications or mobile apps, this grant type doesn't involve issuing a client secret. Access tokens are returned immediately to the client.
- Password Grant: Allows the client to exchange the user's credentials for an access token. Not recommended for public clients due to security concerns.
- Client Credentials Grant: Our focus here, this grant type enables server-to-server communication without involving user authentication. It's ideal for situations where the client itself is the resource owner or when users are not involved in the communication.

## The Client Credentials Flow: Benefits and Use Cases

The Client Credentials Flow, a key part of OAuth 2.0, is particularly suited for scenarios where a client application (typically a server) needs to access resources on its own behalf, without acting on behalf of a user. 
This makes it perfect for server-to-server communication, where two backend servers need to interact securely and efficiently.

Benefits of the Client Credentials Flow:

- Efficiency: The absence of user involvement streamlines the communication process, reducing unnecessary redirects and interactions.
- Security: Sensitive user credentials are not exposed in the communication, enhancing security.
- Scope-Based Access: Access tokens can be tailored to have limited permissions, ensuring that the client only accesses what it needs.
- Token Revocation: In case of security breaches, access tokens can be revoked independently, minimizing the impact.

The Client Credentials Flow, while extremely useful, should be chosen carefully based on the specific use case. For user-centric interactions, other grant types that involve user authentication might be more suitable.

Now that we've established a solid understanding of OAuth 2.0 and the significance of the Client Credentials Flow, let's transition to the practical realm. 
We'll explore how AWS Cognito can be configured to facilitate this flow, ensuring secure and seamless server-to-server communication.

## Setting Up the Scenario

In this scenario, we'll have 2 Web backend applications communicating to each other.

First one will be Articles Service - a service exposing a public API to manage articles posted on behalf of the company.
In our OAuth setup, this application will act as a Client.

The second application is User Schedule Service - a service responsible for schedules management for particular users.
This service will have protected API and thus will act as the Resource Server in our flow.

Adding AWS Cognito User Pool with configured app client and a resource server along with the server scopes would complete the picture.
The illustration of the flow is provided below:

![Client Credentials Flow](images/client_credentials_flow.png)

## Configuring the Authorization Server

First, we configure our Authorization Server for our applications to be able to authorize themselves, request and validate tokens and check the scopes.
To do that, we need to first sign in to the AWS Console and select the region where AWS Cognito is available.

The steps for the Authorization Server configuration would include:
1. Cognito User Pool and App Client creation
2. Creating a Resource Server with Custom Scopes
3. Assigning the Scopes to the newly created App Client
4. Create a Domain as an entrypoint for our app client

We'll proceed with the creation of a user pool, which is a selectable option at AWS Cognito main panel.

### Creating a User Pool and an App Client

At Step 1 we need to select creation of a User Pool specifically, as Federated Identity Providers are mainly used in cases when there is an external source of users which needs to be connected to AWS in order for these users to get access to AWS services.
Sign-in options are not mandatory for selection in our specific case, as we are not planning to have actual users in our pool at the moment.
However, this might come in handy when we start to expand our system and start integrating user interactions. And once sign in options are configured, they can't be changed later. 

![Step 1](images/cognito_1.png)

At Step 2, we configure password, MFA and account recovery policies, which do not affect our server-to-server communication flow, and impact only user experience.
So, feel free to specify desirable options.

At Step 3, you can enable or disable self registration for the users. This is an important option, as it may make your user pool publicly available and anyone in the internet would be able to register in it.
We'll disable this option for our pool for security purposes.
Apart from that, you can also configure user account verification flow and user attribute changes confirmation flow - which do not impact server-to-server communications.

![Step 3](images/cognito_2.png)

Step 4 is about email messages delivery to end users in case of different kinds of their interactions with our pool.
We'll switch the delivery mechanism to AWS Cognito here for configuration simplicity.

![Step 4](images/cognito_3.png)

Step 5 is important. Here we need to specify a unique name for our user pool and configure an initial app client.
User Pool Name can't be changed once set, so we need to name it carefully. Hosted UI configurations are out of scope of our case, so we'll skip these options.

![Step 5 - User Pool Name](images/cognito_5.png)

Initial app client name in our case can match the name of our service which would act as the client.
We need to select the Confidential Client type, as the communication would not require a user intervention or any other flows via browser.
Plus we'll need to select Client Secret generation in order to successfully implement Client Credentials Flow.

![Step 5 - App Client](images/cognito_6.png)

Advanced app client settings are quite important for us as they impact access tokens lifecycle (Access Token Expiration for our case - we'll keep default 60 minutes).
They also contain some important sign in settings for user perspective, which we won't touch now.
Note that all settings related to Refresh Tokens are not of our concern for our particular case, because Client Credentials Flow does not support Refresh Tokens.

![Step 5 - Advanced app client settings](images/cognito_7.png)

Don't forget to set the tags for the resource, for it to be more easily manageable within your ecosystem, and hit Create.

Once we are at the screen of our newly created user pool, we would like to note the User Pool Id, which is automatically generated by AWS. We will need this one later in our configs.

### Creating a Resource Server with Custom Scopes

Now when we have a User Pool and an App Client for Articles Service created, we now need to create a configuration for our Resource Server, which is User Schedule Service.
To do that, we need to proceed to the App Integration blade at our User Pool dashboard and find Resource Server section.
Proceed with creation of a Resource Server.

![Resource Servers - Empty](images/cognito_8.png)

At the creation screen, we need to provide a name for the resource server, ID (this one is important as it will be later used in the app configs), and custom scopes.
We put the same name for server name and server id for our case, and specify 2 custom scopes: one for reading schedules, and one for updating them. 
This corresponds to our protected APIs in User Schedule Service, and we would like to assign these scopes partially to different clients - in order to have role-based access control between our communicating servers.

![Resource Servers - Creation](images/cognito_9.png)

### Assigning the Scopes to the App Client

Now we need to assign the scopes to our newly created articles-service app client for our app to be able to call specific APIs of user-schedule-service Resource Server.
To do that, we need to go back to our App Integration blade of our User Pool and select our articles-service app client:

![App Client Selection](images/cognito_10.png)

Then we head to Hosted UI section and click Edit.
There we need to select the identity provider the client would be able to sign in through (our Cognito User Pool), grant type (Client Credentials for our case) and the scopes assigned to the client.
For demonstration purposes, we will specifically deselect schdule.update scope and select the schedule.read scope - to show that the APIs will be available to the client only if the corresponding scope is assigned.

![App Client Configuration](images/cognito_11.png)

### Create a Cognito Domain

The last step would be to configure a domain name for our Authorization Server, so that our app client would be able to request an access token from AWS Cognito, in order to send it to the resource server.
We need to head back to the App Integration blade of our Cognito User Pool, and select either Create a Cognito Domain or Create a Custom Domain in front of the Domain section:

![Domain](images/cognito_12.png)

We will select Cognito Domain for simplicity.
All we need to do next is to set up a unique domain name for our authorization server endpoints:

![Domain Creation](images/cognito_12.png)

### Testing the Configuration

After the setup done, we only need to test if our configuration works correctly, and we can obtain an access token.

We will refer to the documentation provided by AWS [here](https://docs.aws.amazon.com/cognito/latest/developerguide/token-endpoint.html) to learn the URI of the access token endpoint.
And we'll try to run a request to see if we get a meaningful access token based on our data.

To compose it, we need the following data:
- Token endpoint hostname: it's the Domain we created at the last step of our configuration.
- Client ID: can be obtained at the corresponding App Client panel for our selected client.
- Client Secret: can be obtained at the corresponding App Client panel for our selected client as well.

For our case, the request would look like the following:

```
curl --location 'https://soname-training-auth.auth.us-east-1.amazoncognito.com/oauth2/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=client_credentials' \
--data-urlencode 'client_id=cousnlblnjks3l1gc7ph18nao' \
--data-urlencode 'client_secret=1r5l7lihjju3ki17ldqnh792f226nlqo4a6pjv08c0p4cmbole0h'
```

Once we execute it, we'll get a JSON response, containing an access token in the corresponding field.

![Token Request](images/token_curl.png)

In essence, the token is in fact a JWT encoded data structure, which includes a whole set of headers, fields (claims) and a signature.
A signature is what makes such kind of tokens secure - a resource server can verify the signature and make sure the tokens have not been tampered with.

Once we decode the token, we'll get the following data structure in the payload:

```
{
  "sub": "cousnlblnjks3l1gc7ph18nao",
  "token_use": "access",
  "scope": "user-schedule-service/schedule.read",
  "auth_time": 1693235716,
  "iss": "https://cognito-idp.us-east-1.amazonaws.com/us-east-1_XQIUFCxoZ",
  "exp": 1693239316,
  "iat": 1693235716,
  "version": 2,
  "jti": "199b847c-0dfe-4995-b34f-b1a2eedda297",
  "client_id": "cousnlblnjks3l1gc7ph18nao"
}
```

Note that it has client ID, expiration date, token type, issuer URL and scopes - all included in the access token.
This information primarily will be used by our Resource Server to validate the access to the API.

All we need to do is to code our applications in that way - to exchange and verify the tokens.

## Configuring the Client

## Configuring the Resource Server

## Testing the Flow