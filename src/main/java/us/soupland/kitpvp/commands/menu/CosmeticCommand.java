package us.soupland.kitpvp.commands.menu;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import us.soupland.kitpvp.utilities.KitPvPUtils;
import us.soupland.kitpvp.utilities.command.KitPvPCommand;
import us.soupland.kitpvp.utilities.inventory.InventoryMaker;

public class CosmeticCommand extends KitPvPCommand {

    public CosmeticCommand() {
        super("cosmetics");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(KitPvPUtils.ONLY_PLAYERS);
            return false;
        }
        Player player = (Player) sender;
        InventoryMaker inventoryMaker = new InventoryMaker("&c&lCosmetics", 3);

      /*  for (int i = 0; i < 27; i++) {
            inventoryMaker.setItem(i, new InventoryMaker.ClickableItem() {
                @Override
                public void onClick(InventoryClickEvent inventoryClickEvent) {

                }

                @Override
                public ItemStack getItemStack() {
                    return new ItemMaker(Material.STAINED_GLASS_PANE).setDisplayname(" ").setDurability(7).create();
                }
            });
        }

        inventoryMaker.setItem(0, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("menu");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lGo Back").create();
            }
        });

        inventoryMaker.setItem(11, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                InventoryMaker maker = new InventoryMaker("&eTitle Selector", 6);
                Profile profile = AxisAPI.getProfile(player);

                maker.setItem(45, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.openInventory(inventoryMaker.getCurrentPage());
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lGo Back").create();
                    }
                });

                maker.setItem(49, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.closeInventory();
                        if (profile.getTag() == null || profile.getTag().getPrefix() == null) {
                            player.sendMessage(ColorText.translate("&cThere is no active title to clear!"));
                        } else {
                            player.sendMessage(ColorText.translate("&cYour current title " + profile.getTag().getPrefix() + " &chas been cleared."));
                            profile.setTag(null);
                            profile.updateProfile();
                        }
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.FENCE).setDisplayname("&cReset Title").create();
                    }
                });

                for (Tag tag : Tag.getTags()) {
                    if (tag.getPrefix() == null) {
                        continue;
                    }
                    maker.addItem(new InventoryMaker.ClickableItem() {
                        @Override
                        public void onClick(InventoryClickEvent inventoryClickEvent) {
                            player.closeInventory();
                            if (profile.getTag() != null && profile.getTag().equals(tag)) {
                                player.sendMessage(ColorText.translate("&cYou already have this Title."));
                            } else if (tag.getPermission() == null || player.hasPermission(tag.getPermission())) {
                                profile.setTag(tag);
                                profile.updateProfile();
                                player.sendMessage(ColorText.translate("&e" + profile.getTag().getPrefix() + " &ehas been set."));
                            } else {
                                player.sendMessage(ColorText.translate("&cYou don't have access to use this Title."));
                            }
                        }

                        @Override
                        public ItemStack getItemStack() {
                            List<String> lore = new ArrayList<>();
                            if (profile.getTag() != null && profile.getTag().equals(tag)) {
                                lore.add("&cYou already have this Title.");
                            } else if (tag.getPermission() == null || player.hasPermission(tag.getPermission())) {
                                lore.add("&aClick to use this &e&lTitle&a.");
                            } else {
                                lore.add("&cYou don't have access to use this Title.");
                            }
                            return new ItemMaker(Material.NAME_TAG).setDisplayname("&7[" + tag.getPrefix() + "&7] &fChat").addLore(lore).create();
                        }
                    });

                }

                player.openInventory(maker.getCurrentPage());
            }

            @Override
            public ItemStack getItemStack() {
                Profile profile = AxisAPI.getProfile(player);
                return new ItemMaker(Material.NAME_TAG).setDisplayname("&e&lTitles").addLore("&7Your current title is " + (profile.getTag() != null && profile.getTag().getPrefix() != null ? profile.getTag().getPrefix() : "&c-")).create();
            }
        });

        inventoryMaker.setItem(12, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                InventoryMaker maker = new InventoryMaker("&6Particle Selector", 3);

                for (ParticleEffect effect : ParticleEffect.values()) {
                    if (effect == ParticleEffect.LAVA || effect == ParticleEffect.RED_DUST || effect == ParticleEffect.SNOWBALL || effect == ParticleEffect.SLIME
                            || effect == ParticleEffect.HEART || effect == ParticleEffect.ANGRY_VILLAGER || effect == ParticleEffect.HAPPY_VILLAGER || effect == ParticleEffect.NOTE
                            || effect == ParticleEffect.PORTAL || effect == ParticleEffect.BUBBLE || effect == ParticleEffect.MOB_SPELL || effect == ParticleEffect.INSTANT_SPELL || effect == ParticleEffect.WITCH_MAGIC) {
                        if (!player.hasPermission("soupland.particle." + effect.name().replace("_", "").toLowerCase())) {
                            continue;
                        }
                        maker.addItem(new InventoryMaker.ClickableItem() {
                            @Override
                            public void onClick(InventoryClickEvent inventoryClickEvent) {
                                player.closeInventory();
                                ProfileManager.getProfile(player).setCurrentParticle(effect);
                                player.sendMessage(ColorText.translate("&eParticle &4&l" + effect.name().replace("_", " ") + " &ehas been set."));
                            }

                            @Override
                            public ItemStack getItemStack() {
                                return new ItemMaker(Material.STAINED_GLASS_PANE).setDurability(AxisUtils.getRandomNumber(15)).setDisplayname("&6&l" + effect.name().replace("_", " ")).addLore("", "&7Click to use this &6Particle", "").create();
                            }
                        });
                    }
                }

                maker.setItem(22, new InventoryMaker.ClickableItem() {
                    @Override
                    public void onClick(InventoryClickEvent inventoryClickEvent) {
                        player.closeInventory();
                        us.soupland.kitpvp.profile.Profile profile = ProfileManager.getProfile(player);
                        if (profile.getCurrentParticle() == null) {
                            player.sendMessage(ColorText.translate("&cThere is no active particle to clear!"));
                        } else {
                            player.sendMessage(ColorText.translate("&cYour current particle has been cleared."));
                            profile.setCurrentParticle(null);
                        }
                    }

                    @Override
                    public ItemStack getItemStack() {
                        return new ItemMaker(Material.FENCE).setDisplayname("&cReset Particle").create();
                    }
                });

                player.openInventory(maker.getCurrentPage());
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.BLAZE_POWDER).setDisplayname("&6&lParticles").create();
            }
        });

        inventoryMaker.setItem(13, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("color");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.INK_SACK).setDurability(1).setDisplayname("&c&lChat Color").create();
            }
        });

        inventoryMaker.setItem(14, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("themes");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.BOOK_AND_QUILL).setDisplayname("&9&lThemes").create();
            }
        });

        inventoryMaker.setItem(15, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {

            }

            @Override
            public ItemStack getItemStack() {
                ItemStack stack = new ItemMaker(Material.SKULL_ITEM).setDurability(3).create();
                SkullMeta skullMeta = (SkullMeta) stack.getItemMeta();
                skullMeta.setOwner("SoupLand");
                skullMeta.setDisplayName(ColorText.translate("&4&lSkin"));
                skullMeta.setLore(Collections.singletonList(ColorText.translate("&7Use /skin <name>")));
                stack.setItemMeta(skullMeta);
                return stack;
            }
        });

        inventoryMaker.setItem(16, new InventoryMaker.ClickableItem() {
            @Override
            public void onClick(InventoryClickEvent inventoryClickEvent) {
                player.performCommand("deathreason");
            }

            @Override
            public ItemStack getItemStack() {
                return new ItemMaker(Material.SKULL_ITEM).setDisplayname("&c&lDeath reason").create();
            }
        });



        player.openInventory(inventoryMaker.getCurrentPage());
*/
        return true;
    }
}