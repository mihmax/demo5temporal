package ua.dp.maxym.demo5.shipment.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ua.dp.maxym.demo5.dto.Initialize;
import ua.dp.maxym.demo5.dto.ShipGoods;
import ua.dp.maxym.demo4.kafka.events.GoodsShipmentFailed;
import ua.dp.maxym.demo4.kafka.events.GoodsShipmentSucceeded;
import ua.dp.maxym.demo4.kafka.producer.ShipmentProducer;
import ua.dp.maxym.demo5.shipment.domain.UserAddress;
import ua.dp.maxym.demo5.shipment.domain.UserAddressRepository;

@Component
public class ShipmentConsumer {

    @Autowired
    private ShipmentProducer shipmentProducer;

    @KafkaListener(topics = "shipment.commands.ship")
    public void listenSend(ConsumerRecord<String, ShipGoods> shipGoodsConsumerRecord) {
        var shipRequest = shipGoodsConsumerRecord.value();
        var address = userAddressRepository.findByUser(shipRequest.user());
        if (address == null) {
            shipmentProducer.failed(new GoodsShipmentFailed(shipRequest.shipmentId(), String.format("Address of user %s not found", shipRequest.user())));
        } else {
            shipmentProducer.succeeded(new GoodsShipmentSucceeded(shipRequest.shipmentId()));
        }
    }

    /*
    @KafkaListener(topics = "shipment.events.shipment.succeeded")
    public void listenSucceeded(ConsumerRecord<String, GoodsShipmentSucceeded> goodsShipmentSucceededConsumerRecord) {
        // do nothing
    }

    @KafkaListener(topics = "shipment.events.shipment.failed")
    public void listenFailed(ConsumerRecord<String, GoodsShipmentFailed> goodsShipmentFailedConsumerRecord) {
        // do nothing
    }
    */
}
