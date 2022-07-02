package dev.projectg.bedrockplayertransfer.forms;

import com.velocitypowered.api.proxy.Player;
import dev.projectg.bedrockplayertransfer.FloodgateHandler;
import dev.projectg.bedrockplayertransfer.VelocityBedrockPlayerTransfer;
import net.kyori.adventure.text.Component;
import org.geysermc.cumulus.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.geysermc.floodgate.api.FloodgateApi;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class VelocityTransferForm {

    public void packetBuilder(Player player){

        UUID uuid = player.getUniqueId();
        List<String> names = VelocityBedrockPlayerTransfer.getPlugin().getProxyServer().getAllPlayers().stream().map(Player::getUsername).collect(Collectors.toList());
        String[] playerList = names.toArray(new String[0]);
        boolean isFloodgatePlayer = FloodgateHandler.isFloodgatePlayer(uuid);
        if (isFloodgatePlayer) {
            FloodgatePlayer fPlayer = FloodgateApi.getInstance().getPlayer(uuid);
            fPlayer.sendForm(
                    CustomForm.builder()
                            .title("传送玩家")
                            .dropdown("选择玩家", playerList)
                            .input("服务器地址")
                            .input("服务器端口")
                            .responseHandler((form, responseData) -> {
                                CustomFormResponse response = form.parseResponse(responseData);
                                if (!response.isCorrect()) {
                                    return;
                                }
                                int clickedIndex = response.getDropdown(0);
                                String serverip = response.getInput(1);
                                int serverport = Integer.parseInt(Objects.requireNonNull(response.getInput(2)));
                                String name = names.get(clickedIndex);
                                Optional<Player> getplayer = VelocityBedrockPlayerTransfer.getPlugin().getProxyServer().getPlayer(name);
                                if (getplayer.isPresent()) {
                                    UUID targetPlayer = getplayer.get().getUniqueId();
                                    boolean isTargetFloodgatePlayer = FloodgateHandler.isFloodgatePlayer(targetPlayer);
                                    if (isTargetFloodgatePlayer) {
                                        new ConfirmationForm().confirmation(targetPlayer, serverip, serverport);
                                    } else {
                                        player.sendMessage(Component.text("你只能传送基岩玩家"));
                                    }
                                }
                            }));
        }
    }
}