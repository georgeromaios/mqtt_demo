package com.example.demo;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

@Configuration
public class MQTT_beans {
	
	//SUBSCIBER
	
	@Bean

	public MqttPahoClientFactory mqttClientFactory() {
		DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
		MqttConnectOptions options = new MqttConnectOptions();
		
		options.setServerURIs(new String[] { "tcp://localhost:1883" });
		//options.setUserName("admin");
		//String pass = "12345678";
		//options.setPassword(pass.toCharArray());
		options.setCleanSession(true);

		factory.setConnectionOptions(options);

		return factory;
	}

	@Bean
	public MessageChannel mqttInputChannel() {
		return new DirectChannel();
	}
	
	@Bean
	public MessageProducer inbound() {
		MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn",
				mqttClientFactory(), "#");

		adapter.setCompletionTimeout(5000);
		adapter.setConverter(new DefaultPahoMessageConverter());
		adapter.setQos(2); //quality of service level
		adapter.setOutputChannel(mqttInputChannel());
		return adapter;
	}
	
	
	@Bean
	@ServiceActivator(inputChannel = "mqttInputChannel")
	public MessageHandler handler() {
		return new MessageHandler() {

			@Override
			public void handleMessage(Message<?> message) throws MessagingException {
				String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
				//Takes a topic value from -t in cmd 
				switch (topic) {
				case "1":
					System.out.println("This is the topic number 1 ");
					break;
				case "2":
					System.out.println("This is the topic number 2 ");
					break;
				case "3":
					System.out.println("This is the topic number 3 ");
					break;
				case "4":
					System.out.println("This is the topic number 4 ");
					break;
				case "5":
					System.out.println("This is the topic number 5 ");
					break;	
				}
				//if(topic.equals("myTopic")) {
				//System.out.println("This is the topic");
				//}
				//message takes the value of the -m input in cmd
				System.out.println(message.getPayload());
			}

		};
	}
	
	//PUBLISH
	@Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
	@Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        //clientId is generated using a random number
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut", mqttClientFactory());
        messageHandler.setAsync(true);	//always listening
        messageHandler.setDefaultTopic("#");
        messageHandler.setDefaultRetained(false);
        return messageHandler;
    }

}
	

