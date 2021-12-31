# Modifiers

great example of composition over inheritance

always add modifier to Composable components:

- customizability and reuse
- Explicit behavior
- preserves parent/child layout relationship

never use FillMax{Size, Width, Height} implicitly

parent should always tell the child how to be layed out, not the other way around. The child should only be concerned with it's own content.
