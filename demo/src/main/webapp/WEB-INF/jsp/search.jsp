<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recherhe rapide emplacement</title>
</head>

<body>
    <h2>Veuillez remplir les informations ci_dessous : </h2>
    <form action="faire-recherche" method="GET">
        <!-- <label for="typeProduit">Le type de produit </label>
            <select name="typeProduit" id="">
                <option value="produit1">test1</option>
                <option value="produit1">test2</option>
            </select><br> -->
        <label for="volume">Volume requis</label>
        <input type="number" name="volume"><br>
        <label for="quantite">Quantite</label>
        <input type="number" name="quantite"><br>
        <button type="submit">Rechercher</button>
    </form>

    <hr>
    <h2>Resultat de la recherche :</h2><br>

    <c:if test="${not empty resultat}">

        <c:choose>
            <c:when test="${resultat == 'Aucun emplacement disponible'}">
                <p>${resultat}</p>
            </c:when>

            <c:otherwise>
                <ul>
                    <c:forEach var="emplacement" items="${resultat}">
                        <li>Emplacement ID : ${emplacement.id} - Volume : ${emplacement.volume}</li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>

    </c:if>
</body>

</html>