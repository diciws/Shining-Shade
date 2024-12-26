-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Erstellungszeit: 26. Jan 2023 um 23:37
-- Server-Version: 10.5.15-MariaDB-0+deb11u1
-- PHP-Version: 7.4.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `shadebot`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `guilds`
--

CREATE TABLE `guilds` (
  `id` varchar(100) NOT NULL,
  `date` varchar(100) NOT NULL,
  `privilegelevel` int(100) NOT NULL,
  `phasmoguessrchannelid` varchar(100) NOT NULL,
  `botchannelid` varchar(100) NOT NULL,
  `guildflag` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `guilds`
--

INSERT INTO `guilds` (`id`, `date`, `privilegelevel`, `phasmoguessrchannelid`, `botchannelid`, `guildflag`) VALUES
('0', '2023-01-23 23:01:01', 100, '0', '0', 0);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `language`
--

CREATE TABLE `language` (
  `languageoutput` varchar(100) NOT NULL,
  `de` text NOT NULL,
  `en` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `language`
--

INSERT INTO `language` (`languageoutput`, `de`, `en`) VALUES
('botjoinsetup.configurator.button_denied', 'Sorry, das dürfen nur Admins.', 'Sorry, only admins can do this.'),
('botjoinsetup.configurator.description.bottom', '[placeholder]', '[placeholder]'),
('botjoinsetup.configurator.description.channel_not_found', 'nicht vorhanden', 'not found'),
('botjoinsetup.configurator.description.your_settings_top', 'Deine Bot-Einstellungen:', 'Your bot settings:'),
('botjoinsetup.configurator.title', 'Konfiguration', 'Configuration'),
('challenges.bronzestatue', '**Bronze Klassiker!** Stelle die Schwierigkeit auf x15. Erledige alle vier Aufgaben und schieße ein Geisterfoto.', '**Bronce classic!** Change the difficulty to 15x. Finish all tasks and take a picture of the ghost.'),
('challenges.fastghost', '**Rasender Geist!** Erhöhe die Geistergeschwindigkeit um 25%, falls möglich!', '**Fast ghost!** Increase the ghost speed by 25%, if its possible!'),
('challenges.goldstatue', '**Gold Klassiker!** Stelle die Schwierigkeit auf x24. Erledige alle vier Aufgaben und schieße ein Geisterfoto.', '**Gold classic!** Change the difficulty to 24x. Finish all tasks and take a picture of the ghost.'),
('challenges.goslow', '**Im Schleichtempo!** Verringere die Spielergeschwindigkeit um 25%, falls möglich!', '**Sneak speed!** Reduce the players speed by 25%, if its possible.'),
('challenges.lessevidence', '**Weniger Beweise!** Verringere die Anzahl der erhaltenen Beweise um 1, falls möglich!', '**Less evidence!** Reduce the  number of evidence by 1, if possible.'),
('challenges.lockedin', '**Locked in!** Einmal das Hausbetreten, darfst du es nur noch verlassen, wenn du abfährst!', '**Locked in!** Once you enter the house, you can\'t leave it til you depart.'),
('challenges.noelectriclight', '**Kein elektronsiches Licht!** Verzichte auf elektronische Lichtquellen jeder Art.', '**No electric lights!** Play without any kind electronic light sources .'),
('challenges.nofoto', '**Keine Fotokamera!** Es ist nicht erlaubt die Fotokamera zu benutzen!!', '**No photos!** Its not allowed to use the photo camera!'),
('challenges.nohiding', '**Keine Verstecke!** Stelle die Anzahl der Verstecke auf \\\"keine\\\".', '**No Hiding spots!** Set the number of hiding spots to \'none\'.'),
('challenges.nosanity', '**Keine Sanity!** 0% Sanity zu Beginn und 0% Wirkung der Sanitypillen einstellen.', '**No sanity!** Select 0% for sanity and 0%  for saintypills.'),
('challenges.nostealth', '**No Stealth!** Beim Hunt muss durchgehend geredet werden oder ein eingeschaltetes Item, wie eine Taschenlampe getragen werden.', '**No stealth!** While hunting you must keep talking or turn on and hold a powered item, for example:Flashlight.'),
('challenges.nowkey', '**No W-Key!** Ändere die Tastenbelegung, sodass die W-Taste nichts mehr macht. Du darfst nicht mehr gerade aus laufen!', '**No W-key!** change your key binding so that the W-key doesn\'t do anything anymore. You are not allowed to walk straight.'),
('challenges.oldscool', '**Altmodisch!** Verwende keine elektronischen Items! Lichtschalter/Sicherungskasten sind aber erlaubt.', '**Old fashion!** Don\'t use any kind of electric itmes, allowed are light switches and fuse box.'),
('challenges.profipictures', '**Profi Fotograf!** Fülle das Fotobuch vollständig mit 1-Stern-Fotos.', '**Professional Photographer!** Fill the whole photo book with 1 star photos.'),
('challenges.silverstatue', '**Silber Klassiker!** Stelle die Schwierigkeit auf x20. Erledige alle vier Aufgaben und schieße ein Geisterfoto.', '**Silver classic!** Change the difficulty to 20x. Finish all tasks and take a picture of the ghost.'),
('challenges.sitdown', '**In die Hocke!** Drücke im Truck C, bevor du dir Items nimmst, belege die Taste um und steh nicht mehr auf.', '**Crouch!** While you are in the truck and before grabbing some items press C, change the C-key binding and dont stand up anymore.'),
('challenges.staythere', '**Standhaft!** Belege die C-Taste um, setze dich nicht mehr hin und bleibe für den Rest der Mission aufrecht.', '**Steadfast!** Change the C Key binding. don\'t crouch for the rest of the mission.'),
('challenges.title', ':game_die: Zufällige Challenge:', ':game_die: Random Challenge:'),
('commands.generic.reply_disabled_in_guild_use_dm', 'Dieser Command ist auf diesem Server nicht aktiviert. Du kannst ihn aber als Privatnachricht an mich nutzen.', 'This command is not active on this server. You can still use it by sending it to me in direct messages.'),
('commands.info.description', ':ghost: **__Standard Befehle:__**\r\n**/shade** ► Zeigt die wichtigsten Commands\r\n**/profile** ► Öffnet dein Profil\r\n**/smudge** ► Startet einen Timer für geräucherte Geister! (60s),(90s) und (180s)\r\n**/gea** ►  (G)host (E)vidence (A)nalyzer, ich schreibe dir eine privat Nachricht mit dem Tool\r\n**/maps** ► Zeigt alle verfügbaren Karten an\r\n**/invite** ► Bot Link sowie Support-Server Link\r\n\r\n:game_die: **__Wirf die Würfel:__**\r\n**/rollmap** ► Wählt eine zufällige Karte aus\r\n**/challenge** ► Wählt dir eine zufällige Aufgabe aus\r\n\r\n:map: **__Mini-Spiele:__**\r\n**/pgstart** ► Startet eine Runde PhasmoGuessr\r\n**/pginfo** ► Infos über PhasmoGuessr\r\n\r\n:pencil: **__Inventarsystem:__**\r\n**/showinventory** ► Zeigt öffentlich dein Inventar\r\n**/additem** ► Fügt ein Item zum Inventar hinzu\r\n**/removeitem** ► Entfernt ein Item aus dem Inventar\r\n**/resetinventory** ► Leert das Inventar', ':ghost: **__Basic commands:__**\r\n**/shade** ► Summarizes the most important commands\r\n**/profile** ► Shows your profile\r\n**/smudge** ► Starts a smudge Timer for Ghosts! (60s),(90s) and (180s)\r\n**/gea** ►  (G)host (E)vidence (A)nalyzer, I provide you this tool in the direct messages\r\n**/maps** ► Shows all available maps\r\n**/invite** ► Bot invite link and support-server link :sparkles:\r\n\r\n:game_die: **__Roll the dice:__**\r\n**/rollmap** ► A random map will be chosen\r\n**/challenge** ► A random challenge will be chosen\r\n\r\n:map: **__Mini-games:__**\r\n**/pgstart** ► Starts a round PhasmoGuessr\r\n**/pginfo** ► Info about PhasmoGuessr\r\n\r\n:pencil: **__Inventory system:__**\r\n**/showinventory** ► Displays your inventory publicly\r\n**/additem** ► Adds an item to your inventory\r\n**/removeitem** ► Removes an item from your inventory\r\n**/resetinventory** ► Clears the inventory'),
('commands.info.title', 'Befehle die du nutzen kannst:', 'Commands you can use:'),
('commands.inventory.showinventory.description_inv_empty', '**Dein Inventar ist leer!**', '**Your inventory is empty!**'),
('commands.invite.title', ':mailbox: Einladungslink:', ':mailbox: Invite link:'),
('commands.maps.appendone', ':small_blue_diamond: Kleine Maps:\r\n', ':small_blue_diamond: Small maps:\r\n'),
('commands.maps.appendtwo', '\r\n:small_orange_diamond: Mittlere/Große Maps:\r\n', '\r\n:small_orange_diamond: Medium/Large maps:\r\n'),
('commands.maps.footer', 'Trotz sorgfältiger inhaltlicher Kontrolle übernehmen wir keine Haftung für die Inhalte externer Links. Für den Inhalt der verlinkten Seiten sind ausschließlich deren Betreiber verantwortlich.', 'Despite careful control of the content, we do not assume any liability for the content of external links. The operators of the linked sites are solely responsible for their content.'),
('commands.maps.title', ':map: Alle Maps in Phasmophobia:', ':map: All Phasmophobia maps:'),
('commands.profile.description', '[UserAsMentioned] deine Informationen:', '[UserAsMentioned] your information:'),
('commands.profile.field_joined_date', 'Auf dem Server seit:', 'On this Server since:'),
('commands.profile.field_points', ' Punkte:', ' Points:'),
('commands.profile.field_username', ' Nutzer:', ' User:'),
('commands.profile.title', ':pencil: Profil:', ':pencil: Profile:'),
('commands.smudgetimer.after_180s', 'Mittlerweile kann selbst ein Spirit wieder hunten. (180s)', 'Now even a Spirit can start a hunt again. (180s)'),
('commands.smudgetimer.after_60s', 'Ein Dämon könnte nun wieder hunten! (60s)', 'A Demon is able to hunt again! (60s)'),
('commands.smudgetimer.after_90s', 'Jetzt können alle Geister außer Spirit wieder hunten! (90s)', 'All ghosts except spirit can hunt again! (90s)'),
('commands.smudgetimer.reply', 'Alles klar, Timer gestartet!', 'Alright, started a timer!'),
('commands.smudgetimer.reply_with_channel', 'Alles klar, Timer gestartet! Ich ping dich gleich in [channel] an.', 'Alright, started a timer! I\'m gonna ping you in [channel] in a moment.'),
('userapplications.gea.description_top', 'Klicke einfach auf die Beweise, um sie einzutragen [green]\r\noder durchzustreichen [red].\r\nDu kannst sogar auf Albtraum Beweise durchstreichen\r\noder den Orb vom Mimik eintragen!', 'Click on the evidence types to fill them in [green]\r\nor to cross them off [red].\r\nEven on Nightmare mode you can cross off the evidence\r\nor fill in the orb of the Mimic!'),
('userapplications.gea.message_top', 'Ich habe dir mal etwas vorbereitet:', 'I prepared this for you:'),
('userapplications.inventory.commands.CommandAddItem.invtitle_added_item', '[item] hinzugefügt!', 'Added [item]!'),
('userapplications.inventory.commands.CommandAddItem.invtitle_inv_full', 'Dein Inventar ist bereits voll!', 'Your inventory is already full!'),
('userapplications.inventory.commands.CommandAddItem.invtitle_item_already_there', '[item] war bereits im Inventar.', '[item] has already been in your inventory.'),
('userapplications.inventory.commands.CommandClearInventory.description_inv_cleared', '**Inventar geleert!**', '**Inventory cleared!**'),
('userapplications.inventory.commands.CommandClearInventory.description_inv_now_empty', 'Das Inventar von [user] ist nun leer.', 'The Inventory of [user] is empty now.'),
('userapplications.inventory.commands.CommandClearInventory.title_inv_cleared', 'Inventar geleert!', 'Inventory cleared!'),
('userapplications.inventory.commands.CommandRemoveItem.description_inv_already_empty', 'Dein Inventar ist bereits leer!', 'Your inventory is already empty!'),
('userapplications.inventory.commands.CommandRemoveItem.title_item_not_found', '[item] wurde nicht im Inventar gefunden!', '[item] not found in your inventory!'),
('userapplications.inventory.commands.CommandRemoveItem.title_item_removed', '[item] entfernt!', 'Removed [item]!'),
('userapplications.inventory.commands.CommandShowInventory.description_inv_empty', '**Dein Inventar ist leer!**', '**Your inventory is empty!**'),
('userapplications.inventory.InventoryManager.btnlabel_clear', 'Leeren', 'Clear'),
('userapplications.inventory.InventoryManager.warning_not_inv_owner', 'Du kannst nur dein eigenes Inventar bearbeiten!\nDas kannst du hiermit tun: ', 'You can only edit your own inventory!\nYou can do this here:'),
('userapplications.inventory.ItemInventory.description_inv_of_user', '**Das Inventar von **[user]', '[user]**\'s inventory**'),
('userapplications.inventory.ItemInventory.description_inv_of_user_empty', 'Das Inventar von [user] ist leer.', '[user]\'s inventory is empty.'),
('userapplications.phasmoguessr.commands.CommandGuess.description_no_game_running', 'Möchtest du eins starten?', 'Do you want to start a round?'),
('userapplications.phasmoguessr.commands.CommandGuess.reply_game_pending', 'Kleinen Moment bitte, das Spiel startet gerade!', 'One moment please. The game is starting!'),
('userapplications.phasmoguessr.commands.CommandGuess.reply_map_already_revealed', 'Die Map ist bereits bekannt, bitte gib einen Tipp für den Raum ab!', 'The Map has already been revealed. Please guess a room!'),
('userapplications.phasmoguessr.commands.CommandGuess.reply_wrong_channel', 'Du kannst diesen Command nur in PhasmoGuessr-Kanälen verwenden. Mit **/pginfo** erhälst du weitere Infos.', 'This command can only be used in PhasmoGuessr channels. Use **/pginfo** for more information.'),
('userapplications.phasmoguessr.commands.CommandGuess.reply_wrong_state', 'Kleinen Moment! Gerade wird nicht geraten.', 'Wait a moment! You can\'t guess right now.'),
('userapplications.phasmoguessr.commands.CommandGuess.title_no_game_running', 'Gerade läuft kein Spiel in diesem Kanal.', 'There is currently no game running in this channel.'),
('userapplications.phasmoguessr.commands.CommandInfo.description', 'Bei PhasmoGuessr geht es darum die Position des Fotografen als erster zu erraten.\nWer nach ein paar Runden die meisten Punkte gesammelt hat, gewinnt.\nMit **/guess** gibst du einen Tipp ab.\n\nKarten, wie [hier](https://phasmo.karotte.org) können helfen, sind aber nicht immer akkurat.\nMit **/pgstart** kannst du eine neue Runde starten.\n\nDen Channel für PhasmoGuessr können Serveradmins mit **/pgsetchannel** festlegen.', 'PhasmoGuessr is about being first to guess the position of the photographer.\nThe player with the most points after a few rounds will win.\nUse **/guess** to send your guess.\n\nMaps like [here](https://phasmo.karotte.org) can help but aren\'t always accurate.\nYou can start a round by using **/pgstart**.\n\nServer admins can set the PhasmoGuessr-channel using **/pgsetchannel**.'),
('userapplications.phasmoguessr.commands.CommandSetChannel.reply_error', 'Ein Fehler ist aufgetreten. Bitte stelle sicher, mir alle nötigen Berechtigungen zu geben, um in diesem Channel Einbettungen zu posten und versuche es erneut.', 'An error occurred. Please make sure to grant me the permissions to send message embeds in this channel and try again.'),
('userapplications.phasmoguessr.commands.CommandSetChannel.reply_success', 'PhasmoGuessr-Channel festgelegt.', 'PhasmoGuessr Channel set.'),
('userapplications.phasmoguessr.commands.CommandSetChannel.testmsg_desc_welcome', 'Willkommen zu PhasmoGuessr! Mit **/pgstart** kann eine Runde gestartet werden.', 'Welcome to PhasmoGuessr! Use **/pgstart** to start a new round.'),
('userapplications.phasmoguessr.commands.CommandStart.reply_game_already_running', 'Es läuft bereits ein Spiel!', 'There is already a game running in this channel!'),
('userapplications.phasmoguessr.commands.CommandStart.reply_wrong_channel', 'Du kannst diesen Command nur in PhasmoGuessr-Kanälen verwenden. Mit **/pginfo** erhälst du weitere Infos.', 'This command can only be used in PhasmoGuessr channels. Use **/pginfo** for more information.'),
('userapplications.phasmoguessr.Game.btnlabel_start_new_round', 'Neue Runde starten', 'Start a new round'),
('userapplications.phasmoguessr.Game.description_correct_both', 'Woah krass, [user] hat beides auf einen Schlag richtig!\r\nDu erhälst [points] Punkte!\r\n', 'Woah crazy, [user] just guessed the correct map and room at once!\r\nYou get [points] points!\r\n'),
('userapplications.phasmoguessr.Game.description_correct_map', '[user] hat die Map richtig erkannt und erhält [points] Punkte!', '[user] identified the map and receives [points] points!'),
('userapplications.phasmoguessr.Game.description_correct_room', '[user] hat nun auch den richtigen Raum herausgefunden!\r\nDafür gibt es [points] Punkte!\r\n', '[user] just found the correct room!\r\nYou received [points] Points!'),
('userapplications.phasmoguessr.Game.description_correct_room_waiting_for_next_img', '\r\nDas nächste Bild kommt in Kürze...', '\r\nThe next screenshot is on its way...'),
('userapplications.phasmoguessr.Game.description_scoreboard_top', 'Das Spiel ist vorbei. Die Gewinner sind:', 'Game over. The winners are:'),
('userapplications.phasmoguessr.Game.description_wrong_guess', '\"[guess]\" ist leider nicht korrekt. Versuche es erneut!', 'Unfortunately [guess] isn\'t correct.\r\nTry again!'),
('userapplications.phasmoguessr.Game.send_img_description', 'Verwende **/guess** um einen Tipp abzugeben.', 'Use **/guess** to make your guess.'),
('userapplications.phasmoguessr.Game.send_img_title', 'Wo wurde dieses Bild aufgenommen?', 'Where has this image been made?'),
('userapplications.phasmoguessr.Game.title_correct_room', 'Richtig!', 'Correct!'),
('userapplications.phasmoguessr.Game.title_scoreboard', 'Herzlichen Glückwunsch!', 'Congratulation!'),
('userapplications.phasmoguessr.Game.title_wrong_guess', 'Falsch!', 'Incorrect!'),
('userapplications.phasmoguessr.PgManager.reply_game_already_running', 'Es läuft bereits ein Spiel!', 'There is already a game running in this channel!'),
('userapplications.phasmoguessr.PgManager.reply_wrong_channel', 'Dies ist leider kein PhasmoGuessr-Channel mehr.', 'This is no longer a PhasmoGuessr channel.');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `phasmoguessr`
--

CREATE TABLE `phasmoguessr` (
  `url` varchar(100) NOT NULL,
  `map` int(100) NOT NULL,
  `room` varchar(100) NOT NULL,
  `imageindex` int(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `phasmoguessr`
--

INSERT INTO `phasmoguessr` (`url`, `map`, `room`, `imageindex`) VALUES
('https://cdn.discordapp.com/attachments/1037077888291315782/1037991644013797406/20220516202018_1.jpg', 3, 'Green Bedroom', 1),
('https://cdn.discordapp.com/attachments/1037077888291315782/1037992604387774525/20220525120040_1.jpg', 2, 'Toilet', 2),
('https://cdn.discordapp.com/attachments/1037077888291315782/1037993495996145664/20220808200252_2.jpg', 1, 'Right Storage Room', 3),
('https://cdn.discordapp.com/attachments/1037077888291315782/1037994225234620447/20220826210838_1.jpg', 1, 'Master Bedroom', 4),
('https://cdn.discordapp.com/attachments/1037077888291315782/1037994910239948810/20220901004036_1.jpg', 0, 'Foyer', 5),
('https://cdn.discordapp.com/attachments/1037077888291315782/1037997292982128660/20220901212110_1.jpg', 3, 'Hallway', 6),
('https://cdn.discordapp.com/attachments/1037077888291315782/1037998183751622686/20220924062708_1.jpg', 1, 'Kitchen', 7),
('https://cdn.discordapp.com/attachments/1037077888291315782/1037998961891479602/20220925190410_1.jpg', 4, 'Foyer', 8),
('https://cdn.discordapp.com/attachments/1037077888291315782/1038000285907435591/20221030221005_1.jpg', 11, 'Padded Cell 1', 9),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040675859251609600/20221111140155_1.jpg', 0, 'Master Bedroom', 10),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040675883691819169/20221111140317_1.jpg', 0, 'Living Room', 11),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040675914582872074/20221111140403_1.jpg', 0, 'Basement', 12),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040675935709560932/20221111140453_1.jpg', 0, 'Basement', 13),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040675965476540426/20221111140610_1.jpg', 0, 'Dining Room', 14),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676031624921088/20221111140714_1.jpg', 0, 'Utility Room', 15),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676048976744549/20221111140834_1.jpg', 0, 'Garage', 16),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676079830052934/20221111140904_1.jpg', 0, 'Garage', 17),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676103158779974/20221111141017_1.jpg', 0, 'Kitchen', 18),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676137786937395/20221111141120_1.jpg', 0, 'Kitchen', 19),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676148792799253/20221111141229_1.jpg', 0, 'Nursery', 20),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676179293769878/20221111141346_1.jpg', 0, 'Main Bathroom', 21),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676234331435069/20221111141413_1.jpg', 0, 'Main Bathroom', 22),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676300769198190/20221111141432_1.jpg', 0, 'Boys Bedroom', 23),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676349091794954/20221111141442_1.jpg', 0, 'Boys Bedroom', 24),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676586887856299/20221111141856_1.jpg', 0, 'Master Bedroom', 25),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676707880931439/20221111142357_1.jpg', 4, 'Twin Bedroom', 26),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676917910720522/20221111142414_1.jpg', 4, 'Twin Bedroom', 27),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676944204791899/20221111142437_1.jpg', 4, 'Twin Bedroom', 28),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676961040744548/20221111142518_1.jpg', 4, 'Living Room', 29),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040676987917840464/20221111142614_1.jpg', 4, 'Living Room', 30),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677015034019941/20221111142645_1.jpg', 4, 'Master Bedroom', 31),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677055215452200/20221111142722_1.jpg', 4, 'Master Bedroom', 32),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677073590681660/20221111142745_1.jpg', 4, 'Master Bedroom', 33),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677171473154140/20221111142929_1.jpg', 4, 'Workshop', 34),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677193430343680/20221111142942_1.jpg', 4, 'Workshop', 35),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677215190388756/20221111143012_1.jpg', 4, 'Kitchen', 36),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677233653727333/20221111143124_1.jpg', 4, 'Kitchen', 37),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677252016381953/20221111143139_1.jpg', 4, 'Dining Room', 38),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677280764141598/20221111143153_1.jpg', 4, 'Dining Room', 39),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677575200092301/20221111143741_1.jpg', 4, 'Storage', 40),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677703906496522/20221111143834_1.jpg', 4, 'Foyer', 41),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677741156106290/20221111143943_1.jpg', 4, 'Nursery', 42),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677839411892274/20221111144320_1.jpg', 4, 'Nursery', 43),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040677855975198811/20221111144339_1.jpg', 4, 'Nursery', 44),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040721996071313490/20221111210228_1.jpg', 8, 'Basketball Court', 45),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040722022495420596/20221111210445_1.jpg', 8, 'Classroom 10', 46),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040722038203105371/20221111210629_1.jpg', 8, 'Cafeteria', 47),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040722056943255612/20221111210719_1.jpg', 8, 'Cafeteria', 48),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040722071627497574/20221111210940_1.jpg', 8, 'Classroom 5', 49),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040722108566737056/20221111211414_1.jpg', 8, 'Storage Room 1', 50),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040722310753177682/20221111211843_1.jpg', 8, 'Classroom 34', 51),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040722585454903358/20221111211949_1.jpg', 8, 'Lecture Hall', 52),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040722830364524555/20221111212051_1.jpg', 8, 'Lecture Hall', 53),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040723102839079012/20221111212151_1.jpg', 8, 'Classroom 23', 54),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040722087918186596/20221111211028_1.jpg', 8, 'Library', 55),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040723390836768878/20221111212305_1.jpg', 8, 'Classroom 24', 56),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040723895889698916/20221111212505_1.jpg', 8, 'Classroom 26', 57),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040724196419981333/20221111212617_1.jpg', 8, 'Classroom 28', 58),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040724495641616424/20221111212729_1.jpg', 8, 'Storage Room 2', 59),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040725078947659806/20221111212948_1.jpg', 8, 'Classroom 31', 60),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040725200544739430/20221111213016_1.jpg', 8, 'Science Classroom 3', 61),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040726159018704966/20221111213400_1.jpg', 8, 'Science Classroom 2', 62),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040727893455675492/20221111214059_1.jpg', 10, 'Inspection', 63),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040728914701594644/20221111214233_1.jpg', 10, 'Main Hallway', 64),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040728941629022291/20221111214303_1.jpg', 10, 'Main Hallway', 65),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040728961853960202/20221111214328_1.jpg', 10, 'Cafeteria', 66),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040728982301184030/20221111214400_1.jpg', 10, 'Cafeteria', 67),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040728996981252168/20221111214413_1.jpg', 10, 'Cafeteria', 68),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040729011883606096/20221111214426_1.jpg', 10, 'Cafeteria', 69),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040729445146820708/20221111214548_1.jpg', 10, 'Cafeteria', 70),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040729464419663922/20221111214629_1.jpg', 10, 'Cafeteria', 71),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040729586276782111/20221111214743_1.jpg', 10, 'Cafeteria', 72),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040730353582739506/20221111215046_1.jpg', 10, 'A Block Hallway', 73),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040730605375197285/20221111215145_1.jpg', 10, 'A Block Hallway', 74),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040730880588656791/20221111215249_1.jpg', 10, 'A Block Hallway', 75),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040731583746949223/20221111215536_1.jpg', 10, 'Outside', 76),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040731786537336933/20221111215627_1.jpg', 10, 'Outside', 77),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040731949213433887/20221111215705_1.jpg', 10, 'B Block Hallway', 78),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040732100485189642/20221111215742_1.jpg', 10, 'B Block Hallway', 79),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040732484649877535/20221111215913_1.jpg', 10, 'B Block Hallway', 80),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040732668276527184/20221111215956_1.jpg', 10, 'B Block Bottom Right', 81),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040733055645667338/20221111220127_1.jpg', 10, 'B Block Bottom Right', 82),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040733657406652467/20221111220314_1.jpg', 10, 'B Block Bottom Left', 83),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040734325353742367/20221111220432_1.jpg', 10, 'Bathroom B', 84),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040734390495498321/20221111220648_1.jpg', 10, 'Bathroom A', 85),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040734857187299398/20221111220833_1.jpg', 10, 'Infirmary Hallway', 86),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040735184221372416/20221111220928_1.jpg', 10, 'Wardens Office', 87),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040735216848863392/20221111220952_1.jpg', 10, 'Wardens Office', 88),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040735424072654858/20221111221054_1.jpg', 10, 'Infirmary Hallway', 89),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040735747998752838/20221111221208_1.jpg', 10, 'Infirmary Hallway', 90),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040735813870305320/20221111221227_1.jpg', 10, 'Infirmary Hallway', 91),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040735944615133264/20221111221254_1.jpg', 10, 'Cafeteria Security', 92),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040736306088656906/20221111221425_1.jpg', 10, 'Infirmary', 93),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040736456961953792/20221111221501_1.jpg', 10, 'Infirmary', 94),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040736929093779506/20221111221653_1.jpg', 10, 'Main Office', 95),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040737446767370300/20221111221856_1.jpg', 10, 'Reception', 96),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040737645938081903/20221111221943_1.jpg', 10, 'Visitation Security', 97),
('https://cdn.discordapp.com/attachments/1026827206082695228/1040738079109042258/20221111222114_1.jpg', 10, 'Entrance', 98);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `settings`
--

CREATE TABLE `settings` (
  `name` varchar(100) NOT NULL,
  `value` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- Daten für Tabelle `settings`
--

INSERT INTO `settings` (`name`, `value`) VALUES
('bot.invite.link', ''),
('bot.key', ''),
('bot.rank.dev', 'BOT-DEV'),
('bot.rank.mod', 'BOT-MOD'),
('bot.rank.tester', 'BOT-TESTER'),
('bot.rank.vip', 'BOT-VIP'),
('bot.supportserver.link', ''),
('bot.test.key', ''),
('bot.vote.link', '');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `users`
--

CREATE TABLE `users` (
  `ID` varchar(100) NOT NULL,
  `Datum` datetime NOT NULL,
  `Permission` varchar(100) NOT NULL,
  `Punkte` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `users`
--

INSERT INTO `users` (`ID`, `Datum`, `Permission`, `Punkte`) VALUES
('1025681540874776626', '2022-11-10 13:55:33', 'User', 8);

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `guilds`
--
ALTER TABLE `guilds`
  ADD PRIMARY KEY (`id`);

--
-- Indizes für die Tabelle `language`
--
ALTER TABLE `language`
  ADD PRIMARY KEY (`languageoutput`);

--
-- Indizes für die Tabelle `phasmoguessr`
--
ALTER TABLE `phasmoguessr`
  ADD PRIMARY KEY (`imageindex`);

--
-- Indizes für die Tabelle `settings`
--
ALTER TABLE `settings`
  ADD PRIMARY KEY (`name`);

--
-- Indizes für die Tabelle `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
