package io.github.blaezdev.rwbym;

import io.github.blaezdev.rwbym.Init.RWBYBiomes;
import io.github.blaezdev.rwbym.Init.RWBYItems;
import io.github.blaezdev.rwbym.capabilities.Aura.AuraProvider;
import io.github.blaezdev.rwbym.capabilities.Aura.IAura;
import io.github.blaezdev.rwbym.capabilities.CapabilityHandler;
import io.github.blaezdev.rwbym.capabilities.Clover.IClover;
import io.github.blaezdev.rwbym.capabilities.ISemblance;
import io.github.blaezdev.rwbym.capabilities.Lysette.ILysette;
import io.github.blaezdev.rwbym.capabilities.Qrow.IQrow;
import io.github.blaezdev.rwbym.entity.EntityBullet;
import io.github.blaezdev.rwbym.entity.EntityGrimm;
import io.github.blaezdev.rwbym.utility.RWBYConfig;
import io.github.blaezdev.rwbym.utility.network.MessageSendPlayerData;
import io.github.blaezdev.rwbym.utility.network.RWBYNetworkHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

import java.util.ArrayList;
import java.util.List;

public class EntityUpdatesHandler {

    @SubscribeEvent
    public void onUpdate(LivingUpdateEvent event) {
        EntityLivingBase entityLiving = event.getEntityLiving();

        if (entityLiving != null && entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entityLiving;

            IAura aura = null;

            if (!player.world.isRemote && player.hasCapability(AuraProvider.AURA_CAP, null)) {
                aura = player.getCapability(AuraProvider.AURA_CAP, null);
                aura.onUpdate(player);
            }

            ISemblance semblance = CapabilityHandler.getCurrentSemblance(player);
            if (semblance != null) {
                semblance.onUpdate(player);
            }
            if (!player.world.isRemote) {
                RWBYNetworkHandler.sendToAll(new MessageSendPlayerData(semblance, aura, player.getEntityData().getCompoundTag(RWBYModels.MODID), player.getName()));
            }
        }
    }


    @SubscribeEvent
    public void playerRespawn(PlayerRespawnEvent e) {
        EntityPlayer player = e.player;
        IAura otheraura = player.getCapability(AuraProvider.AURA_CAP, null);
        otheraura.setAmount(0);
        otheraura.addAmount(otheraura.getMaxAura());
        //System.out.println("Respawn");
    }

    @SubscribeEvent
    public void firstJoin(PlayerLoggedInEvent event) {
        EntityPlayer player = event.player;
        IAura otheraura = player.getCapability(AuraProvider.AURA_CAP, null);
        NBTTagCompound entityData = player.getEntityData();
        if (!entityData.getBoolean(RWBYModels.MODID + "joinedBefore")) {
            entityData.setBoolean(RWBYModels.MODID + "joinedBefore", true);
            if (RWBYConfig.general.enablefirstspawnscroll) {
                player.inventory.addItemStackToInventory(new ItemStack(RWBYItems.scroll));
            }
            otheraura.setAmount(otheraura.getMaxAura());
        }
    }

    @SubscribeEvent
    public void onBlockDropItems(BlockEvent.HarvestDropsEvent event) {
        if (event.getHarvester() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getHarvester();
            if (event.getState().getMaterial() == Material.PLANTS && player.getHeldItemMainhand().getItem() == RWBYItems.leafshield || event.getState().getBlock().getMaterial(null) == Material.PLANTS && player.getHeldItemOffhand().getItem() == RWBYItems.leafshield) {
                List<ItemStack> drops = new ArrayList<ItemStack>();
                drops.addAll(event.getDrops());
                for (int i = 0; i < 1; ++i) {
                    event.setDropChance(1.0F);
                    event.getDrops().addAll(drops);
                    System.out.println(event.getDrops().toString());
                }
            }
        }

    }

    @SubscribeEvent
    public void onLivingDropsEvent(LivingDropsEvent event) {

        Entity entity = event.getEntity();
        World world = event.getEntity().getEntityWorld();
        BlockPos pos = event.getEntity().getPosition();
        ArrayList<Item> itemset1 = new ArrayList<>();
        itemset1.add(RWBYItems.lichtroze_closedfire);
        itemset1.add(RWBYItems.lichtroze_closedice);
        itemset1.add(RWBYItems.lichtroze_closedwind);
        ArrayList<Item> itemset2 = new ArrayList<>();
        itemset2.add(RWBYItems.heroshield);
        itemset2.add(RWBYItems.leafshield);
        itemset2.add(RWBYItems.pickaxeshield);
        itemset2.add(RWBYItems.rageshield);
        Entity killer = event.getSource().getTrueSource();
        if (event.getSource().getDamageType().equals("player")) {
            if (entity instanceof EntityGrimm && killer instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
                //mh equals the item held by the Player
                Item mh = player.getHeldItem(EnumHand.MAIN_HAND).getItem();
                if (itemset1.contains(mh)) {
                    ItemStack stack = new ItemStack(RWBYItems.peach);
                    EntityItem itemDropX = new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, stack);
                    event.getDrops().add(itemDropX);
                }
                if (itemset2.contains(mh)) {
                    ItemStack stack = new ItemStack(RWBYItems.remnants);
                    EntityItem itemDrop1 = new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, stack);
                    EntityItem itemDrop2 = new EntityItem(world, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, stack);
                    event.getDrops().add(itemDrop1);
                    event.getDrops().add(itemDrop2);
                }
            }
        }
    }

    @SubscribeEvent
    public void onEntityDamageLast(LivingHurtEvent event) {
        EntityLivingBase entityliving = event.getEntityLiving();
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ISemblance semblance = CapabilityHandler.getCurrentSemblance(player);
            float attackdamage = event.getAmount();
            if (semblance instanceof IClover) {
                int level = semblance.getLevel();
                if (level == 1) {
                    float eventdamage = attackdamage * 1.2f;
                    event.setAmount(eventdamage);
                }
                if (level == 2) {
                    float eventdamage = attackdamage * 1.3f;
                    event.setAmount(eventdamage);
                }
                if (level == 3) {
                    float eventdamage = attackdamage * 1.4f;
                    event.setAmount(eventdamage);
                }
            }
        }

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ISemblance semblance = CapabilityHandler.getCurrentSemblance(player);
            if (semblance instanceof ILysette) {
                int level = semblance.getLevel();
                PotionEffect potion = new PotionEffect(MobEffects.SLOWNESS, level * 150, 128, true, false);
                if (semblance.isActive()) {
                    entityliving.addPotionEffect(potion);
                    player.getCapability(AuraProvider.AURA_CAP, null).useAura(player, 15F, false);
                }
            }
        }

        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ISemblance semblance = CapabilityHandler.getCurrentSemblance(player);
            float attackdamage = event.getAmount();
            if (semblance instanceof IQrow) {
                int level = semblance.getLevel();
                if (level == 1) {
                    float eventdamage = attackdamage * (player.getEntityWorld().rand.nextFloat() + 0.5F);
                    event.setAmount(eventdamage);
                }
                if (level == 2) {
                    float eventdamage = attackdamage * (player.getEntityWorld().rand.nextFloat() + 0.6F);
                    event.setAmount(eventdamage);
                }
                if (level == 3) {
                    float eventdamage = attackdamage * (player.getEntityWorld().rand.nextFloat() + 0.7F);
                    event.setAmount(eventdamage);
                }

            }
        }
    }

    @SubscribeEvent
    public void onEntityDamageFirst(LivingHurtEvent event) {
        EntityLivingBase entityliving = event.getEntityLiving();
        if (RWBYConfig.aura.aurablockdamage) {
            if (entityliving instanceof EntityPlayer && !entityliving.world.isRemote) {
                EntityPlayer player = (EntityPlayer) entityliving;
                if (player.hasCapability(AuraProvider.AURA_CAP, null)) {
                    IAura aura = player.getCapability(AuraProvider.AURA_CAP, null);
                    float playerdamagereduction = aura.getMaxAura() / RWBYConfig.aura.playerdamagetoaurareduction;
                    float eventamount = event.getAmount() * 5;
                    if (playerdamagereduction > 0.5F) {
                        playerdamagereduction = 0.5F;
                    }
                    if (RWBYConfig.aura.aurareduction) {
                        if (event.getSource().getTrueSource() instanceof EntityPlayer || event.getSource().getTrueSource() instanceof EntityBullet || event.getSource().getTrueSource() instanceof EntityArrow) {
                            float overflow = aura.useAura(player, eventamount * playerdamagereduction, true);
                            aura.delayRecharge(600);
                            event.setAmount(overflow / 5);
                        } else {
                            float overflow = aura.useAura(player, event.getAmount() * 5, true);
                            aura.delayRecharge(600);
                            event.setAmount(overflow / 5);
                        }
                    } else {
                        float overflow = aura.useAura(player, event.getAmount() * 5, true);
                        aura.delayRecharge(600);
                        event.setAmount(overflow / 5);
                    }
                }
            }
        }

    }


    @SubscribeEvent
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        RWBYModels.LOGGER.log(RWBYModels.debug, "Player Respawn");

        EntityPlayer player = event.player;

        ISemblance semblance = CapabilityHandler.getCurrentSemblance(player);

        if (semblance != null && player.hasCapability(semblance.getCapability(), null)) {
            RWBYModels.LOGGER.log(RWBYModels.debug, "Player has Semblance: " + semblance + ", with Level: " + ((ISemblance) player.getCapability(semblance.getCapability(), null)).getLevel());
        }
    }

    @SubscribeEvent
    public void onPlayerClone(Clone event) {
        ISemblance semblance = CapabilityHandler.getCurrentSemblance(event.getOriginal());

        int level = semblance.getLevel();

        semblance = CapabilityHandler.getCapabilityByName(event.getEntityPlayer(), semblance.getCapability().getName());

        semblance.setLevel(level);

        IAura oldaura = event.getOriginal().getCapability(AuraProvider.AURA_CAP, null);

        IAura newaura = event.getEntityPlayer().getCapability(AuraProvider.AURA_CAP, null);

        newaura.deserialize((NBTTagCompound) oldaura.serialize());

        event.getEntityPlayer().getEntityData().merge(event.getOriginal().getEntityData());
    }

    @SubscribeEvent
    public void onPlayerLoggedOn(PlayerLoggedInEvent event) {

        ISemblance semblance = CapabilityHandler.getCurrentSemblance(event.player);

        if (semblance == null) {

            List<ISemblance> semblances = CapabilityHandler.getAllSemblances(event.player);

            semblance = semblances.get(event.player.world.rand.nextInt(semblances.size()));

            if (semblance == null) {
                RWBYModels.LOGGER.error("Could not Get A Semblance for Player {}", event.player.getDisplayNameString());
            } else {
                RWBYModels.LOGGER.log(RWBYModels.debug, "Set Semblance for Player {} to {}", event.player.getDisplayNameString(), semblance);
                semblance.setLevel(1);
            }
        }

        RWBYModels.LOGGER.log(RWBYModels.debug, "Player {} Logged On With Semblance {}", event.player.getDisplayNameString(), semblance);

    }

    @SubscribeEvent
    public void clientConnectedToServer(ClientConnectedToServerEvent event) {
        RWBYModels.LOGGER.log(RWBYModels.debug, "Client Connected");
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        Biome biome = player.world.getBiome(player.getPosition());


        if (biome == RWBYBiomes.GrimmWastes && player.isInWater()) {
            PotionEffect potioneffect = new PotionEffect(MobEffects.POISON, 60, 3, false, false);
            PotionEffect potioneffect1 = new PotionEffect(MobEffects.WITHER, 60, 2, false, false);
            player.addPotionEffect(potioneffect);
            player.addPotionEffect(potioneffect1);
        }


    }
}
