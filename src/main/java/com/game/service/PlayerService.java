package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class PlayerService {

    private final Date date2000 = new Date(100, Calendar.JANUARY, 1);
    private final Date date3000 = new Date(1000, Calendar.JANUARY,1);
    
    @Autowired
    private PlayerRepository playerRepository;

    @Transactional
    public List<Player> getFilteredListPlayers(
            String name,
            String title,
            Race race,
            Profession profession,
            Long after,
            Long before,
            String banned,
            Integer minExperience,
            Integer maxExperience,
            Integer minLevel,
            Integer maxLevel) {
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);

        List<Player> result = new ArrayList<>();

        playerRepository.findAll().forEach(player -> {
            if (name != null && !player.getName().contains(name)) return;
            if (title != null && !player.getTitle().contains(title)) return;
            if(race != null && player.getRace() != race) return;
            if (profession != null && player.getProfession() != profession) return;
            if (after != null && player.getBirthday().before(afterDate)) return;
            if (before != null && player.getBirthday().after(beforeDate)) return;
            if (banned != null && player.getBanned() != Boolean.valueOf(banned)) return;
            if (minExperience != null && player.getExperience().compareTo(minExperience) < 0) return;
            if (maxExperience != null && player.getExperience().compareTo(maxExperience) > 0) return;
            if (minLevel != null && player.getLevel().compareTo(minLevel) < 0) return;
            if (maxLevel != null && player.getLevel().compareTo(maxLevel) > 0) return;
            result.add(player);
        });
        return result;
   }

   @Transactional
   public Player saveNewPlayer(Player player) {

        if (
                player.getName() == null
                || player.getTitle() == null
                || player.getRace() == null
                || player.getProfession() == null
                || player.getBirthday() == null
                || player.getExperience() == null
                || player.getName().length() > 12
                        || player.getTitle().length() > 30
                        || player.getExperience() < 0
                        || player.getExperience() > 10000000
                || player.getBirthday().before(new Date(date2000.getTime()))
                || player.getBirthday().after(new Date(date3000.getTime()))
        ) throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);
        player.setLevel((int)Math.sqrt(((double) 2500+200 * player.getExperience()) - 50) / 100);
        player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
        return playerRepository.save(player);
    }

    public Player updatePlayer(Player player, Long id) {
        Player result;

        if (id == 0)
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);
        else if(playerRepository.findById(id).equals(Optional.empty()))
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND);

        try {
            if (player.getExperience() < 0 || player.getExperience() > 10000000)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (player.getBirthday() != null && player.getBirthday().getTime() < 0)
              throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            if (player.getBirthday() != null && !player.getBirthday().after(new Date(date2000.getTime())) && !player.getBirthday().before(new Date(date3000.getTime())))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            result = playerRepository.findById(id).get();
            if (player.getName() != null && player.getName().length() <= 12)
                result.setName(player.getName());
            if (player.getTitle() != null && player.getTitle().length() <= 30)
                result.setTitle(player.getTitle());
            if (player.getRace() != null)
                result.setRace(player.getRace());
            if (player.getProfession() != null)
                result.setProfession(player.getProfession());
            if (player.getBirthday() != null)
                result.setBirthday(player.getBirthday());
            if (player.getExperience() != null) {
                result.setExperience(player.getExperience());
                result.setLevel((int) (Math.sqrt((double) 2500 + 200 * player.getExperience()) - 50) / 100);
                result.setUntilNextLevel(50 * (result.getLevel() + 1) * (result.getLevel() + 2) - player.getExperience());
                if (player.getBanned() != null)
                    result.setBanned(player.getBanned());
            }
            playerRepository.save(result);
            return result;
    }

    public Player getPlayerById(Long id) {
        Player player;

        if (id <= 0) {
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if(!playerRepository.existsById(id)) {
            throw  new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        player = playerRepository.findById(id).get();
        return player;
    }


    public void delete(Player player) {
        playerRepository.delete(player);
    }

    @Transactional
    public List<Player> getSortedPlayers(List<Player> filteredPlayers, String page, String countOnPage, String order){
        int pageNum = Integer.parseInt(page)+1;
        int count = Integer.parseInt(countOnPage);
        List<Player> sortedPlayers = new ArrayList<>();
        if (order.equalsIgnoreCase("NAME"))
            filteredPlayers.sort(Comparator.comparing(Player::getName));
        else if (order.equalsIgnoreCase("EXPERIENCE"))
            filteredPlayers.sort(Comparator.comparing(Player::getExperience));
        else if (order.equalsIgnoreCase("BIRTHDAY"))
            filteredPlayers.sort(Comparator.comparing(Player::getBirthday));
        for (int i = pageNum*count-(count-1)-1; i < count*pageNum && i < filteredPlayers.size(); i++) {
            sortedPlayers.add(filteredPlayers.get(i));
        }
        return sortedPlayers;
    }
}
