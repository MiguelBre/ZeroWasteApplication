package br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.modelCatador

import br.senai.jandira.sp.zerowastetest.models.modelretrofit.modelAPI.modelUser.UserData
data class Catador(

    var id: Int = 0,
    var id_usuario: Int = 0,
    var materiais_catador: List<MateriaisCatador>? = null

)


// "id": "fc88207f-fd8e-4d87-9745-95a6fbfa7295",
//                "id_usuario": "99d3f8c2-99e4-4fbc-a388-313bdd9abff0",
//                "materiais_catador": [
//                    {
//                        "id": "0a923586-87ba-4d48-883d-c50331667f55",
//                        "id_materiais": "c9dfd397-abd6-42e8-9d72-61edd880b328",
//                        "id_catador": "fc88207f-fd8e-4d87-9745-95a6fbfa7295",
//                        "material": {
//                            "id": "c9dfd397-abd6-42e8-9d72-61edd880b328",
//                            "nome": "Ferro"
//                        }
//                    }
//                ]