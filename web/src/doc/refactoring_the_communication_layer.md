# Refactoring the communication layer

## Motivation

The communication layer has several problems:

* too many events are passed between client and server
* updates must be immediate as they are bound to the UI Elements - when either client or server changes an immediate update is necessary to keep them in sync.
* the handling of the single events requires a lot of hand-coding to react on that certain event.

These deficiencies should be overcome and replaced by a more efficient approach.

## Goals

* decouple event processing (make them asynchronous) between client and server
* reduce client-server babble
* early execution of certain actions so that some changes can be done right away in the client and sending notification to server afterwards

## Possible road

A central question is whether to use a client-side model or not. At least is seems to be attractive for the following reasons:

* when using a client-side model the updates of the controls/components does not need to be hand-coded as its done by the lib.
* by representing the whole state in the client we are free to defer updates to the server - all changes can be passed as a batch. This meets the goal of decoupling both.
* to a certain degree it would allow to use local storage to work offline (restriction being 'hidden' instance data not represented on the client - needs some thorough consideration)

The alternative is to go without a model and (as is) apply server-side control updates to the client. It's not fully clear to me if that by design hinders the goal of decoupling.

After all using a client-side model seems to have clear advantages. It should be clearly notes that the model is a kind of UI-model or View-model. It will consist of a flat list of 'records'.

- id
- value
- readonly
- relevant
- enabled
- valid
- type
- index (when repeat item)
- TEXT child

A control will bind to the id in the model. This id corresponds to the id on the server. 

Note:
the referencing is another question: there's the option of binding directly to instance nodes instead of referencing via ids of controls (as is now). However that will create other problems (bind expression language resp. mapping of that). For now it would be much easier to go with the existing ids (and don't change much about that mechanism in the first step).




