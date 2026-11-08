import json, sys
CAMPOS = {"cliente", "productos", "fecha_entrega", "urgente", "es_pedido"}
def validar(texto):
   datos = json.loads(texto) # falla si no es JSON
   assert set(datos) == CAMPOS, f"Campos incorrectos: {set(datos)}"
   assert isinstance(datos["productos"], list), "productos debe ser una lista"
   for p in datos["productos"]:
    assert set(p) == {"nombre", "cantidad"}, f"Producto mal formado: {p}"
    assert p["cantidad"] is None or isinstance(p["cantidad"], int)
   assert isinstance(datos["urgente"], bool) and isinstance(datos["es_pedido"], bool)
   return datos
print(validar(open(sys.argv[1], encoding="utf-8").read()))
