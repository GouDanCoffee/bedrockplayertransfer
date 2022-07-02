package dev.projectg.bedrockplayertransfer.forms;

import com.velocitypowered.api.proxy.Player;
import dev.projectg.bedrockplayertransfer.FloodgateHandler;
import dev.projectg.bedrockplayertransfer.TransferPacketBuilder;
import dev.projectg.bedrockplayertransfer.VelocityBedrockPlayerTransfer;
import net.kyori.adventure.text.Component;
import org.geysermc.cumulus.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.Optional;
import java.util.UUID;

public class ConfirmationForm {
    public void confirmation(UUID target, String ip, int port) {

        boolean isFloodgatePlayer = FloodgateHandler.isFloodgatePlayer(target);
        if (isFloodgatePlayer) {
            FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(target);
            fPlayer.sendForm(
                    SimpleForm.builder()
                            .title("你想要连接至 " + ip + "?")
                            .button("是")
                            .button("否")
                            .responseHandler((form, responseData) -> {
                                SimpleFormResponse response = form.parseResponse(responseData);
                                if (!response.isCorrect()) {
                                    // player closed the form or returned invalid info (see FormResponse)
                                    return;
                                }
                                if (response.getClickedButtonId() == 0) {
                                    // clicked Yes
                                    new TransferPacketBuilder().sendPacket(ip, port, target);
                                } else if (response.getClickedButtonId() == 1) {
                                    // clicked No
                                    Optional<Player> player = VelocityBedrockPlayerTransfer.getPlugin().getProxyServer().getPlayer(target);
                                    player.ifPresent(p -> p.sendMessage(Component.text("你拒绝了传送")));
                                }
                            }));
        }
    }
}