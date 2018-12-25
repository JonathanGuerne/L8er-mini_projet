# Introduction 

La8er est un projet Android écrit avec le langage Kotlin qui a été réalisé dans le cadre du cours de système d'exploitation mobile du master MSE de la HES-SO. Il a pour but de fournir à l'utilisateur un moyen de planifier des SMS dans le temps. Cette fonctionnalité est intéressante dans beaucoup de cas différents comme : ne pas oublier d'envoyer un message important, écrire une pensée à un proche, ...
L8er est pensé pour être une application intuitive qui ne demandera à l'utilisateur que peut ou même pas d'apprentissage avant de commencer à l'utiliser. 

Dans un second temps La8er offira des fonctionalités plus avancées tel que l'envoi de SMS basé sur la géolocalisation, la création de groupe de contact ou encore la possibilité d'ajouté des variables dans le texte du message. 

# Mise en place

Trello, un outils de gestion en ligne, à été utilisé pour simplifier la répartition des tâches sur le projet. Les tâches ont été divisées en backend et frontend regroupant respectivement les tâches liées à la logique de l'application et celle liée à l'esthétique. Il était très facile de travailler en parallèle sur le backend et le frontend car les tâches ne rentrait que rarement en conflit. 

# Technologies 

Dans un premier temps il a fallut commencer à appréhender Kotlin. Cela c'est avéré plutôt facile car il est basé sur le Java un lanage bien connu et que son intégration au projet était aussi simplifier par Android studio l'IDE utilisée pour le développement. 

## Envoi de SMS
L'envoi de SMS se fait grâce au ```SmsManager``` fournit par Android. Il offre la possibilité d'envoyer un message texte à un certain numéro en passant par l'application SMS par défaut du téléphone. 

```Kotlin
SmsManager.getDefault().
  sendTextMessage(number, null, text, null, null)
```

On pourrait également spécifier des intents à lancer en cas de succès/d'échec.


## Stockage dans la base de données

## Affichage des SMS à la page d'accueil 


# Conclusion 

## Avis personnels 



## Améliorations possibles 

Comme mentionné dans l'introduction une des améliorations possibles serait l'ajout d'un système permettant d'envoyer un sms en fonction de la géolocalisation de l'utilisateur. Egalement l'utilisateur pourrait créer des groupes de contacts qui permetteraient de facilement envoyer des messages planifiés à tout un ensemble de personnes. 
