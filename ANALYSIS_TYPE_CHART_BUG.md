# Type Chart Bug Analysis & Solution

## Problem Summary
The randomizer crashes when reopening a ROM that has been edited with a type chart editor, while TEE Master can open edited ROMs without issue.

## Root Cause
**Location:** `Gen3RomHandler.java` - `readTypeTable()` method (lines 4061-4098)

**The Bug:**
```java
Type attacking = Gen3Constants.typeTable[attackingType];  // Line 4069
Type defending = Gen3Constants.typeTable[defendingType];  // Line 4070
```

When reading the type effectiveness table:
1. The code reads byte values for attacking/defending types from ROM
2. It uses these bytes as array indices into `Gen3Constants.typeTable`
3. **Problem:** If the ROM was previously edited and contains type bytes outside the original 18 types, the array returns `null`
4. Calling `typeTable.setEffectiveness(null, ...)` throws `IllegalArgumentException: Type null not supported`

**Example Scenario:**
- Original Fire Red has types 0x00-0x11 (excluding 0x09 = ???-type)
- User edits ROM with TEE Master, adds a new type at byte 0x12
- Type table in ROM now has entries like: `0x12 0x03 0x14` (new type -> POISON = double damage)
- When randomizer reads this: `Gen3Constants.typeTable[0x12]` returns `null` because array only goes up to 0x11
- **CRASH!**

## How TEE Master Handles This Correctly

From `TEE-master/TEE3/Form1.cs` - `LoadTable()` method (lines 290-383):

```csharp
// Key differences:
1. Starts with empty/minimal table
2. DYNAMICALLY RESIZES as it reads entries:
   if (a + 1 > table.Length) {
       Array.Resize(ref table, a + 1);
   }
3. Creates lists for ANY type byte found in ROM
4. Doesn't assume fixed type count
5. Reads until terminator (0xFE/0xFF)
```

**TEE Master's Approach:**
- Reads type effectiveness entries sequentially
- For each entry (attacking, defending, effectiveness):
  - If type byte is larger than current array, resize array
  - Add entry to the dynamically-sized table
- Result: Can handle ANY type configuration, including custom types

## Proposed Solution

**Option 1: Defensive Reading (SIMPLE - RECOMMENDED)**
Skip unknown type entries instead of crashing:

```java
private TypeTable readTypeTable() {
    TypeTable typeTable = new TypeTable(Type.getAllTypes(3));
    int currentOffset = romEntry.getIntValue("TypeEffectivenessOffset");
    int attackingType = rom[currentOffset];
    while (attackingType != GBConstants.typeTableTerminator) {
        if (rom[currentOffset] != GBConstants.typeTableForesightTerminator) {
            int defendingType = rom[currentOffset + 1];
            int effectivenessInternal = rom[currentOffset + 2];
            
            // FIX: Check if types are valid before using them
            Type attacking = (attackingType >= 0 && attackingType < Gen3Constants.typeTable.length) 
                ? Gen3Constants.typeTable[attackingType] : null;
            Type defending = (defendingType >= 0 && defendingType < Gen3Constants.typeTable.length) 
                ? Gen3Constants.typeTable[defendingType] : null;
            
            // Only process if BOTH types are recognized
            if (attacking != null && defending != null) {
                Effectiveness effectiveness;
                switch (effectivenessInternal) {
                    case 20: effectiveness = Effectiveness.DOUBLE; break;
                    case 10: effectiveness = Effectiveness.NEUTRAL; break;
                    case 5: effectiveness = Effectiveness.HALF; break;
                    case 0: effectiveness = Effectiveness.ZERO; break;
                    default: effectiveness = null; break;
                }
                if (effectiveness != null) {
                    typeTable.setEffectiveness(attacking, defending, effectiveness);
                }
            }
            // If types are unknown, silently skip - this allows reading edited ROMs
        }
        currentOffset += 3;
        attackingType = rom[currentOffset];
    }
    return typeTable;
}
```

**Key Changes:**
1. ✅ Bounds check before array access
2. ✅ Only process entries with valid types
3. ✅ Silently skip unknown types instead of crashing
4. ✅ Can now open previously edited ROMs
5. ✅ Maintains compatibility with unmodified ROMs

**Trade-offs:**
- ✅ Simple fix, minimal code changes
- ✅ Won't crash on edited ROMs
- ⚠️ Unknown types are ignored (not preserved if you re-save)
- ⚠️ Type chart editor will only show vanilla types

## Option 2: Full Custom Type Support (COMPLEX)
Would require:
- Modifying TypeTable to support dynamic type lists
- Updating GUI to display custom types
- Much more invasive changes
- Not necessary if goal is just "don't crash on edited ROMs"

## Recommendation
**Use Option 1** - it's simple, safe, and solves your immediate problem:
1. Can reopen edited ROMs without errors
2. Preserves the type table for vanilla types
3. Doesn't break existing functionality
4. Easy to implement and test

The trade-off is that custom types added by other editors won't be fully preserved, but the ROM will remain functional and you can still edit all vanilla type matchups.
