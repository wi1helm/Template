package nub.wi1helm.template.npc;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.metadata.PlayerMeta;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.network.packet.server.play.DestroyEntitiesPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.packet.server.play.SetPassengersPacket;

import java.util.List;

public enum SpawnStrategy {
    NONE {
        @Override
        public void spawn(TemplateNPC npc, Player player){}

        @Override
        public void despawn(TemplateNPC npc, Player player) {

        }
    },
    SITTING {
        @Override
        public void spawn(TemplateNPC npc, Player player) {
            System.out.println("Spawning " + npc.getName() + " in a sitting position.");

            // Create a mount entity (Armor Stand)
            Entity mount = new Entity(EntityType.ARMOR_STAND);
            mount.editEntityMeta(ArmorStandMeta.class, meta -> {
                meta.setInvisible(true);
                meta.setHasNoGravity(true);
                meta.setMarker(true); // Ensures it does not interfere with collisions
            });

            // Set mount position slightly lower for sitting effect
            Pos mountPosition = npc.getSpawnPosition().add(0, -mount.getEntityType().height(), 0);
            mount.setInstance(npc.getInstance(), mountPosition);

            // Ensure previous instance is removed
            player.sendPacket(new DestroyEntitiesPacket(mount.getEntityId()));

            // Attach NPC to mount
            mount.addPassenger(npc);
            player.sendPacket(new SetPassengersPacket(mount.getEntityId(), List.of(npc.getEntityId())));
            // Spawn the mount
            player.sendPacket(mount.getEntityType().registry().spawnType().getSpawnPacket(mount));
            mount.updateNewViewer(player);

            // Ensure NPC metadata is applied (e.g., skin layers)
            npc.editEntityMeta(PlayerMeta.class, meta -> {
                npc.getSkinLayer().apply(meta);
            });

            // Ensure NPC name is displayed above correctly
            double offset = 0.3;
            double baseHeight = npc.getEntityType().height();

            npc.getName().getText().forEach((integer, entity) -> {
                Pos hologramPos = npc.getSpawnPosition().add(0, baseHeight + (integer) * offset, 0);
                entity.setInstance(npc.getInstance(), hologramPos);
                player.sendPacket(entity.getMetadataPacket());
                player.sendPacket(entity.getEntityType().registry().spawnType().getSpawnPacket(entity));

                entity.updateNewViewer(player);
            });

            // Ensure NPC is visible for players
            npc.updateNewViewer(player);
        }

        @Override
        public void despawn(TemplateNPC npc, Player player) {
            // Remove NPC's name tag
            npc.getName().getText().forEach((integer, entity) -> {
                player.sendPacket(new DestroyEntitiesPacket(entity.getEntityId()));
                entity.updateOldViewer(player);
            });

            // Remove the NPC itself
            if (npc.getEntityType() == EntityType.PLAYER) {
                player.sendPacket(new PlayerInfoRemovePacket(npc.getUuid()));
            } else {
                player.sendPacket(new DestroyEntitiesPacket(npc.getEntityId()));
            }

            // Remove the mount
            if (npc.getPassengers().size() > 0) {
                npc.getPassengers().forEach(entity -> {
                    player.sendPacket(new DestroyEntitiesPacket(entity.getEntityId()));
                    entity.updateOldViewer(player);
                });


            }

            npc.updateOldViewer(player);
        }
    },
    STANDING {
        @Override
        public void spawn(TemplateNPC npc, Player player) {

            // Check if the NPC already has the AI goal before adding it
            boolean alreadyHasGoal = npc.getAIGroups().stream()
                    .flatMap(group -> group.getGoalSelectors().stream()) // Get all goal selectors
                    .anyMatch(goal -> goal.equals(npc.getGoal())); // Check if it already exists

            if (!alreadyHasGoal && npc.getGoal() != null) {
                npc.addAIGroup(
                        new EntityAIGroupBuilder()
                                .addGoalSelector(npc.getGoal())
                                .build()
                );
            }



            npc.editEntityMeta(PlayerMeta.class, meta -> {
                npc.getSkinLayer().apply(meta); // Apply full skin layers
            });

            double offset = 0.3;
            double baseHeight = npc.getEntityType().height();

            npc.getName().getText().forEach((integer, entity) -> {
                Pos hologramPos = npc.getSpawnPosition().add(0, baseHeight + (integer) * offset, 0);
                entity.setInstance(npc.getInstance(), hologramPos);
                player.sendPacket(entity.getMetadataPacket());
                player.sendPacket(entity.getEntityType().registry().spawnType().getSpawnPacket(entity));

                entity.updateNewViewer(player);
            });

            if (npc.getEntityType() == EntityType.PLAYER) {
                List<PlayerInfoUpdatePacket.Property> properties = (npc.getSkin() != null)
                        ? List.of(new PlayerInfoUpdatePacket.Property("textures", npc.getSkin().textures(), npc.getSkin().signature()))
                        : List.of();

                PlayerInfoUpdatePacket.Entry entry = new PlayerInfoUpdatePacket.Entry(
                        npc.getUuid(), npc.getIdentifier(), properties, false, 0, GameMode.SURVIVAL, null, null
                );

                player.sendPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry));
            }

            npc.updateNewViewer(player);
        }

        @Override
        public void despawn(TemplateNPC npc, Player player) {
            npc.getName().getText().forEach((integer, entity) -> {
                player.sendPacket(new DestroyEntitiesPacket(entity.getEntityId()));
                entity.updateOldViewer(player);
            });

            if (npc.getEntityType() == EntityType.PLAYER) {
                player.sendPacket(new PlayerInfoRemovePacket(npc.getUuid()));
            } else {
                player.sendPacket(new DestroyEntitiesPacket(npc.getEntityId()));
            }

            npc.updateOldViewer(player);
        }
    },
    LAYING {
        @Override
        public void spawn(TemplateNPC npc, Player player) {

            npc.getAIGroups().clear(); // Removes all AI goals

            npc.editEntityMeta(PlayerMeta.class, meta -> {
                npc.getSkinLayer().apply(meta); // Apply full skin layers
                npc.setPose(Entity.Pose.SLEEPING);
            });

            double offset = 0.3;
            double baseHeight = npc.getEntityType().height() / 2;

            npc.getName().getText().forEach((integer, entity) -> {
                Pos hologramPos = npc.getSpawnPosition().add(0, baseHeight + (integer) * offset, 0);
                entity.setInstance(npc.getInstance(), hologramPos);
                player.sendPacket(entity.getMetadataPacket());
                player.sendPacket(entity.getEntityType().registry().spawnType().getSpawnPacket(entity));

                entity.updateNewViewer(player);
            });

            if (npc.getEntityType() == EntityType.PLAYER) {
                List<PlayerInfoUpdatePacket.Property> properties = (npc.getSkin() != null)
                        ? List.of(new PlayerInfoUpdatePacket.Property("textures", npc.getSkin().textures(), npc.getSkin().signature()))
                        : List.of();

                PlayerInfoUpdatePacket.Entry entry = new PlayerInfoUpdatePacket.Entry(
                        npc.getUuid(), npc.getIdentifier(), properties, false, 0, GameMode.SURVIVAL, null, null
                );

                player.sendPacket(new PlayerInfoUpdatePacket(PlayerInfoUpdatePacket.Action.ADD_PLAYER, entry));
            }

            npc.updateNewViewer(player);
        }

        @Override
        public void despawn(TemplateNPC npc, Player player) {
            npc.getName().getText().forEach((integer, entity) -> {
                player.sendPacket(new DestroyEntitiesPacket(entity.getEntityId()));
                entity.updateOldViewer(player);
            });

            if (npc.getEntityType() == EntityType.PLAYER) {
                player.sendPacket(new PlayerInfoRemovePacket(npc.getUuid()));
            } else {
                player.sendPacket(new DestroyEntitiesPacket(npc.getEntityId()));
            }

            npc.updateOldViewer(player);
        }
    };

    // Abstract method for each strategy to implement
    public abstract void spawn(TemplateNPC npc, Player player);

    public abstract void despawn(TemplateNPC npc, Player player);
}
