@file:Suppress("OVERRIDE_DEPRECATION")

package testbukkit

import com.destroystokyo.paper.Namespaced
import com.google.common.collect.Multimap
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.chat.BaseComponent
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.BlockState
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.BlockStateMeta
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.tags.CustomItemTagContainer
import org.bukkit.persistence.PersistentDataContainer

/** Need to implement all methods because so that hashcode/equals works */
open class TestItemMeta(
	val material: Material
) : ItemMeta {
	private var _lore: MutableList<Component>? = null
	private var _displayName: Component? = null

	override fun hasLore(): Boolean = _lore != null
	override fun lore(): MutableList<Component>? { return _lore }
	override fun lore(lore: MutableList<Component>?) { _lore = lore }

	override fun hasDisplayName(): Boolean = _displayName != null
	override fun displayName(): Component? = _displayName
	override fun displayName(displayName: Component?) { _displayName = displayName }

	override fun hashCode(): Int {
		return hashAll(material, _lore, _displayName)
	}

	override fun equals(other: Any?): Boolean {
		return when {
			this === other -> true
			javaClass != other?.javaClass -> false
			hashCode() == other.hashCode() -> true
			else -> false
		}
	}

	override fun clone(): ItemMeta {
		return TestItemMeta(material).also {
			it._lore = _lore
			it._displayName = _displayName
		}
	}

	override fun getLore(): MutableList<String>? { throw NotImplementedError() }
	override fun setLore(lore: MutableList<String>?) { throw NotImplementedError() }
	override fun getDisplayName(): String { throw NotImplementedError() }
	override fun getDisplayNameComponent(): Array<BaseComponent> { throw NotImplementedError() }
	override fun setDisplayName(name: String?) { throw NotImplementedError() }
	override fun setDisplayNameComponent(component: Array<out BaseComponent?>?) { throw NotImplementedError() }
	override fun serialize(): MutableMap<String, Any> { throw NotImplementedError() }
	override fun getPersistentDataContainer(): PersistentDataContainer { throw NotImplementedError() }
	override fun hasLocalizedName(): Boolean { throw NotImplementedError() }
	override fun getLocalizedName(): String { throw NotImplementedError() }
	override fun setLocalizedName(name: String?) { throw NotImplementedError() }
	override fun getLoreComponents(): MutableList<Array<BaseComponent>>? { throw NotImplementedError() }
	override fun setLoreComponents(lore: MutableList<Array<BaseComponent>>?) { throw NotImplementedError() }
	override fun hasCustomModelData(): Boolean { throw NotImplementedError() }
	override fun getCustomModelData(): Int { throw NotImplementedError() }
	override fun setCustomModelData(data: Int?) { throw NotImplementedError() }
	override fun hasEnchants(): Boolean { throw NotImplementedError() }
	override fun hasEnchant(ench: Enchantment): Boolean { throw NotImplementedError() }
	override fun getEnchantLevel(ench: Enchantment): Int { throw NotImplementedError() }
	override fun getEnchants(): MutableMap<Enchantment, Int> { throw NotImplementedError() }
	override fun addEnchant(ench: Enchantment, level: Int, ignoreLevelRestriction: Boolean): Boolean { throw NotImplementedError() }
	override fun removeEnchant(ench: Enchantment): Boolean { throw NotImplementedError() }
	override fun hasConflictingEnchant(ench: Enchantment): Boolean { throw NotImplementedError() }
	override fun addItemFlags(vararg itemFlags: ItemFlag) { throw NotImplementedError() }
	override fun removeItemFlags(vararg itemFlags: ItemFlag) { throw NotImplementedError() }
	override fun getItemFlags(): MutableSet<ItemFlag> { throw NotImplementedError() }
	override fun hasItemFlag(flag: ItemFlag): Boolean { throw NotImplementedError() }
	override fun isUnbreakable(): Boolean { throw NotImplementedError() }
	override fun setUnbreakable(unbreakable: Boolean) { throw NotImplementedError() }
	override fun hasAttributeModifiers(): Boolean { throw NotImplementedError() }
	override fun getAttributeModifiers(): Multimap<Attribute, AttributeModifier>? { throw NotImplementedError() }
	override fun getAttributeModifiers(slot: EquipmentSlot): Multimap<Attribute, AttributeModifier> { throw NotImplementedError() }
	override fun getAttributeModifiers(attribute: Attribute): MutableCollection<AttributeModifier>? { throw NotImplementedError() }
	override fun addAttributeModifier(attribute: Attribute, modifier: AttributeModifier): Boolean { throw NotImplementedError() }
	override fun setAttributeModifiers(attributeModifiers: Multimap<Attribute, AttributeModifier>?) { throw NotImplementedError() }
	override fun removeAttributeModifier(attribute: Attribute): Boolean { throw NotImplementedError() }
	override fun removeAttributeModifier(slot: EquipmentSlot): Boolean { throw NotImplementedError() }
	override fun removeAttributeModifier(attribute: Attribute, modifier: AttributeModifier): Boolean { throw NotImplementedError() }
	override fun getAsString(): String { throw NotImplementedError() }
	override fun getCustomTagContainer(): CustomItemTagContainer { throw NotImplementedError() }
	override fun setVersion(version: Int) { throw NotImplementedError() }
	override fun getCanDestroy(): MutableSet<Material> { throw NotImplementedError() }
	override fun setCanDestroy(canDestroy: MutableSet<Material>?) { throw NotImplementedError() }
	override fun getCanPlaceOn(): MutableSet<Material> { throw NotImplementedError() }
	override fun setCanPlaceOn(canPlaceOn: MutableSet<Material>?) { throw NotImplementedError() }
	override fun getDestroyableKeys(): MutableSet<Namespaced> { throw NotImplementedError() }
	override fun setDestroyableKeys(canDestroy: MutableCollection<Namespaced>) { throw NotImplementedError() }
	override fun getPlaceableKeys(): MutableSet<Namespaced> { throw NotImplementedError() }
	override fun setPlaceableKeys(canPlaceOn: MutableCollection<Namespaced>) { throw NotImplementedError() }
	override fun hasPlaceableKeys(): Boolean = false
	override fun hasDestroyableKeys(): Boolean = false
}

class TestDamageable(
	material: Material
) : TestItemMeta(material), Damageable {

	private var _damage: Int = 0

	override fun clone(): Damageable {
		return TestDamageable(material).also {
			it.lore(this.lore())
			it.displayName(this.displayName())
			it._damage = damage
		}
	}

	override fun hasDamage(): Boolean = _damage > 0
	override fun getDamage(): Int = _damage
	override fun setDamage(damage: Int) {
		_damage = damage
	}
}

class TestBlockStateMeta(
	material: Material
) : TestItemMeta(material), BlockStateMeta {
	private var _blockState: BlockState? = null

	override fun clone(): TestBlockStateMeta {
		return TestBlockStateMeta(material).also {
			it.lore(this.lore())
			it.displayName(this.displayName())
			it.blockState = this.blockState
		}
	}

	override fun hasBlockState(): Boolean = _blockState != null
	override fun getBlockState(): BlockState = _blockState ?: throw IllegalStateException("Missing state")
	override fun setBlockState(blockState: BlockState) {
		this._blockState = blockState
	}
}

fun hashAll(vararg parts: Any?): Int {
	var res = 0
	for (v in parts) {
		res += v.hashCode()
		res *= 31
	}
	return res
}
