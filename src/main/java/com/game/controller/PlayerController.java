package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class PlayerController {
    @Autowired
    private PlayerService playerService;

    @GetMapping(value = "/rest/players")
    public @ResponseBody List<Player> getPlayersList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) String banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(defaultValue = "0", value = "pageNumber") String pageNumber,
            @RequestParam(defaultValue = "3", value = "pageSize") String pageSize,
            @RequestParam(defaultValue = "ID", value = "order") String order) {

        List<Player> list = playerService.getFilteredListPlayers(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        List<Player> playerList = playerService.getSortedPlayers(list, pageNumber, pageSize, order);
        return playerList;
    }

    @PostMapping(value = "/rest/players")
    public @ResponseBody Player createPlayer(@RequestBody Player player) {
        return playerService.saveNewPlayer(player);
    }

    @PostMapping(value = "/rest/players/{id}")
    public @ResponseBody Player updatePlayer(@RequestBody(required = false) Player player, @PathVariable (value = "id", required = false) Long id) {
        if (player.getName() == null && player.getTitle() == null && player.getRace() == null && player.getProfession() == null && player.getBirthday() == null && player.getExperience() == null) {
            return playerService.getPlayerById(id);
        }
        Player resultPlayer = playerService.updatePlayer(player, id);
        return resultPlayer;
    }

    @GetMapping(value = "/rest/players/{id}")
    public @ResponseBody Player getPlayer(@PathVariable (value = "id") Long id) {
        return playerService.getPlayerById(id);
    }

    @DeleteMapping(value = "/rest/players/{id}")
    public void deletePlayer(@PathVariable(value = "id") Long id) {
        Player player = playerService.getPlayerById(id);
        playerService.delete(player);
    }

    @GetMapping(value = "/rest/players/count")
    public @ResponseBody int getPlayersCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) String banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel) {
       List<Player> playerList = playerService.getFilteredListPlayers(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
       return playerList.size();
    }


}

