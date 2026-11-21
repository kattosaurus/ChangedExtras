package us.kattosaur.changedextras.datagen;

import com.google.gson.JsonParser;
import net.minecraft.data.PackOutput;

public final class ModAbilityProvider extends ModJsonProvider {
    public ModAbilityProvider(PackOutput output) {
        super(output, "Ability Data: changedextras");
    }

    @Override
    protected void generate() {
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/choco_conekat", "female",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:choco_conekat/female" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/choco_conekat", "male",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:choco_conekat/male" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/conekat", "female",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:conekat/female" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/conekat", "male",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:conekat/male" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_artist",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_artist" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changedextras:paint_ball"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changedextras:swing"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changedextras:puncture"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_carneline",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_carneline" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:hypnosis"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_catte",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_catte" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_haku",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_haku" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:access_chest"
                    },
                    {
                      "type": "changed:enable_feature",
                      "feature": "changed:flight"
                    },
                    {
                      "type": "changed:attribute_modifier",
                      "attribute": "changed:max_flight_stamina",
                      "method": "add",
                      "factor": 8.0
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_hazzy",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_hazzy" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_jammer",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_jammer" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changedextras:jam"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_kaiju",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_kaiju" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changedextras:generate_strange_crystal"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_katto",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_katto" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changedextras:parry"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changedextras:turn_feral"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_panther",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_panther" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changedextras:prepare"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_thorniii",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_thorniii" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:enable_feature",
                      "feature": "changed:breathe/accept_water"
                    },
                    {
                      "type": "changed:enable_feature",
                      "feature": "changed:aquatic/prevent_sinking"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:create_inkball"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_hands"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "latex_white_cat",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:latex_white_cat" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_transfur_mode"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changedextras:turn_feral"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/mint_conekat", "female",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:mint_conekat/female" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/mint_conekat", "male",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:mint_conekat/male" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/neapolitan_conekat", "female",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:neapolitan_conekat/female" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/neapolitan_conekat", "male",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:neapolitan_conekat/male" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "proto_bee",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:proto_bee" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:enable_feature",
                      "feature": "changed:grab/absorption"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:grab_entity"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "scp_009",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:scp_009" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/strawberry_conekat", "female",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:strawberry_conekat/female" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default/strawberry_conekat", "male",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:strawberry_conekat/male" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:toggle_night_vision"
                    },
                    {
                      "type": "changed:unlock_active_ability",
                      "ability": "changed:switch_gender"
                    }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/nodes/default", "void_cat",
                new JsonParser().parse("""
                {
                  "parent": { "tree": "changedextras:void_cat" },
                  "titleId": "",
                  "price": 0,
                  "acquiredEffects": [
                    { "type": "changed:unlock_active_ability", "ability": "changed:switch_transfur_mode" },
                    { "type": "changed:unlock_active_ability", "ability": "changed:grab_entity" },
                    { "type": "changed:unlock_active_ability", "ability": "changed:toggle_night_vision" }
                  ]
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/choco_conekat", "female",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:choco_conekat/female"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/choco_conekat", "male",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:choco_conekat/male"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/conekat", "female",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:conekat/female"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/conekat", "male",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:conekat/male"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_artist",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_artist"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_carneline",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_carneline"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_catte",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_catte"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_haku",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_haku"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_hazzy",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_hazzy"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_jammer",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_jammer"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_kaiju",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_kaiju"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_katto",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_katto"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_panther",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_panther"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_thorniii",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_thorniii"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "latex_white_cat",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:latex_white_cat"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/mint_conekat", "female",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:mint_conekat/female"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/mint_conekat", "male",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:mint_conekat/male"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/neapolitan_conekat", "female",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:neapolitan_conekat/female"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/neapolitan_conekat", "male",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:neapolitan_conekat/male"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "proto_bee",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:proto_bee"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "scp_009",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:scp_009"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/strawberry_conekat", "female",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:strawberry_conekat/female"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees/strawberry_conekat", "male",
                new JsonParser().parse("""
                {
                  "variants": [
                    "changedextras:strawberry_conekat/male"
                  ],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
        write(PackOutput.Target.DATA_PACK, "ability/trees", "void_cat",
                new JsonParser().parse("""
                {
                  "variants": ["changedextras:void_cat"],
                  "titleId": "",
                  "hidden": true
                }
                """).getAsJsonObject());
    }
}
