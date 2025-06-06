/*
    Copyright (C) 2025  Val_Mobile

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.val_mobile.iceandfire;

import me.val_mobile.rsv.RSVPlugin;
import me.val_mobile.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;

import javax.annotation.Nonnull;

public class LightningExplosionAttack extends ExplosionAttack {

    public LightningExplosionAttack(EnderDragon dragon, Location target, RSVPlugin plugin) {
        super(dragon, target, plugin);
    }

    public LightningExplosionAttack(Dragon dragon, Location target, RSVPlugin plugin) {
        super(dragon, target, plugin);
    }

    @Override
    public void grief(Location center, int radius) {
        World world = center.getWorld();

        Location loc;
        Location secondLoc;
        Block block;
        Material blockMaterial;
        Material electrified;

        Block secondBlock;

        for (int x = -radius; x < radius; x++) {
            for (int y = -radius; y < radius; y++) {
                for (int z = -radius; z < radius; z++) {
                    // if the location is within the specified radius
                    if (!(Math.hypot(Math.hypot(x, y), z) > radius)) {

                        // store the upper and lower block info
                        loc = center.clone().add(x, y, z);
                        secondLoc = center.clone().add(x, y - 1, z);
                        block = loc.getBlock();
                        blockMaterial = block.getType();
                        secondBlock = secondLoc.getBlock();

                        // if the upper block is air or translucent like grass
                        if (block.isEmpty() || !blockMaterial.isOccluding()) {
                            if (!immuneBlocks.contains(blockMaterial.toString())) {
                                if (!secondBlock.isEmpty()) {
                                    world.getBlockAt(loc).setType(getElectrocutedMaterial(blockMaterial, Material.valueOf(config.getString("Dragon.LightningDragon.ExplosionAttack.LayerBlock"))));
                                }
                            }
                        }
                        // if the upper block is solid
                        else {
                            // check if the upper block is immune to breath attacks
                            if (!immuneBlocks.contains(blockMaterial.toString())) {
                                if (!secondBlock.isEmpty()) {
                                    world.getBlockAt(loc).setType(getElectrocutedMaterial(blockMaterial, Material.valueOf(config.getString("Dragon.LightningDragon.ExplosionAttack.ElectrifiedBlock"))));
                                }
                            }
                        }
                    }
                }
            }
        }

        world.createExplosion(center, (float) (config.getDouble("Dragon.LightningDragon.ExplosionAttack.Explosion.Power") * config.getDouble("Dragon.LightningDragon.ExplosionAttack.StageMultipliers.Stage" + DragonUtils.getStage(dragon))), config.getBoolean("Dragon.LightningDragon.ExplosionAttack.SetFire"), config.getBoolean("Dragon.LightningDragon.ExplosionAttack.BreakBlocks"), dragon);
    }

    @Nonnull
    public Material getElectrocutedMaterial(@Nonnull Material material, @Nonnull Material defaultMaterial) {
        if (specialBlocks.containsKey(material.toString())) {
            String[] mats = specialBlocks.get(material.toString());
            return Material.valueOf(mats[Utils.getRandomNum(0, mats.length - 1)]);
        }
        else {
            return defaultMaterial;
        }
    }
}
